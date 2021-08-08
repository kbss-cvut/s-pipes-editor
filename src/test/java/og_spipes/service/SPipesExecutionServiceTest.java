package og_spipes.service;

import og_spipes.model.spipes.ExecutionDTO;
import og_spipes.model.spipes.TransformationDTO;
import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.persistence.dao.TransformationDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SPipesExecutionServiceTest {

    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    private final ScriptDAO scriptDAO = Mockito.mock(ScriptDAO.class);

    private final TransformationDAO transformationDAO = Mockito.mock(TransformationDAO.class);

    private final SPipesExecutionService service = new SPipesExecutionService(
            "http://localhost:1111",
            "pConfigURL",
            "http://localhost:1111/rdf4j-workbench",
            restTemplate,
            transformationDAO,
            scriptDAO
    );

    @Test
    public void serviceExecution(){
        when(restTemplate.getForEntity(
                "http://localhost:1111/service?firstName=karel&_pConfigURL=pConfigURL&id=execute-greeding",
                String.class
        )).thenReturn(new ResponseEntity<>("body", HttpStatus.ACCEPTED));

        String entity = service.serviceExecution(
                "execute-greeding",
                new HashMap<String, String>() {{
                    put("firstName","karel");
                }}
        );

        Assertions.assertEquals("body", entity);
    }

    @Test
    public void moduleExecution(){
        when(restTemplate.postForEntity(
                eq("http://localhost:1111/module?_pConfigURL=pConfigURL&id=moduleId"),
                any(HttpEntity.class),
                anyObject()
        )).thenReturn(new ResponseEntity<>("module executed", HttpStatus.ACCEPTED));

        String entity = service.moduleExecution(
                "moduleInput",
                "moduleId",
                new HashMap<>()
        );

        Assertions.assertEquals("module executed", entity);

    }


    @Test
    public void getAllExecution() throws IOException {
        Map<String, Set<Object>> properties = new HashMap<>();
        properties.put("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-name", Collections.singleton("http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.2"));
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