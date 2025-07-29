package og_spipes.service;

import kong.unirest.MockClient;
import kong.unirest.*;
import og_spipes.model.spipes.ExecutionDTO;
import og_spipes.model.spipes.TransformationDTO;
import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.persistence.dao.TransformationDAO;
import og_spipes.service.exception.SPipesEngineException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SPipesExecutionServiceTest {

    private final ScriptDAO scriptDAO = Mockito.mock(ScriptDAO.class);

    private final TransformationDAO transformationDAO = Mockito.mock(TransformationDAO.class);

    private final SPipesExecutionService service = new SPipesExecutionService(
            "http://localhost:1111",
            "pConfigURL",
            "http://localhost:1111/rdf4j-workbench",
            transformationDAO,
            scriptDAO
    );

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @Test
    public void serviceExecution() throws SPipesEngineException {
        MockClient mock = MockClient.register();

        mock.expect(HttpMethod.GET, "http://localhost:1111/service?firstName=karel&_pConfigURL=pConfigURL&id=execute-greeting")
                .thenReturn("body");

        String entity = service.serviceExecution(
                "execute-greeting",
                new HashMap<String, String>() {{
                    put("firstName","karel");
                }}, "serviceExecutionTest.ttl"
        );

        Assertions.assertEquals("body", entity);
    }

    @Test
    public void moduleExecution() throws IOException, SPipesEngineException {
        MockClient mock = MockClient.register();
        mock.expect(HttpMethod.POST, "http://localhost:1111/module?_pConfigURL=/tmp/config*.ttl&id=http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels")
                .thenReturn("body");
        File f = new File("src/test/resources/scripts_test/sample/skosify/metadata.ttl");

        String entity = service.moduleExecution(
                f.getAbsolutePath(),
                "turtleInput",
                "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels",
                new HashMap<>()
        );

        Assertions.assertEquals("body", entity);
    }


    @Test
    public void getAllExecution() throws IOException {
        Map<String, Set<Object>> properties = new HashMap<>();
        properties.put("http://onto.fel.cvut.cz/ontologies/s-pipes/has-script", Collections.singleton("http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.2"));
        properties.put("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/has-part", Collections.singleton("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation/1618874296751000"));
        properties.put("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-start-date", Collections.singleton(new Date(1619039405731L)));
        properties.put("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-finish-date", Collections.singleton(new Date(1619039432986L)));
        properties.put("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-duration", Collections.singleton(642));
        TransformationDTO transformationDTO = new TransformationDTO("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation/1618874296751000", properties);

        when(transformationDAO.find(URI.create("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation/1618874296751000"))).thenReturn(transformationDTO);
        when(transformationDAO.getAllExecutionTransformation()).thenReturn(Stream.of(
                transformationDTO
        ).collect(Collectors.toList()));
        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("scripts_test/sample/hello-world/hello-world2.sms.ttl")).getFile());
        when(scriptDAO.findScriptByOntologyName(any())).thenReturn(file);

        List<ExecutionDTO> allExecution = service.getAllExecution();

        Assertions.assertEquals(1, allExecution.size());
    }

}