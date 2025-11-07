package og_spipes.service;

import og_spipes.persistence.dao.OntologyDao;
import og_spipes.persistence.dao.ScriptDAO;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sys.JenaSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OntologyHelperTest {

    @Mock
    private ScriptDAO scriptDao;

    @InjectMocks
    private OntologyHelper ontologyHelper;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @BeforeAll
    public static void initJena() {
        JenaSystem.init();
    }

    @Test
    public void getOntologyUri() {
        //TODO parametrize later
        String res = OntologyDao.getOntologyUri(new File("src/test/resources/scripts_test/sample/sample-script.ttl"));

        Assertions.assertEquals("http://www.semanticweb.org/sample-script", res);
    }

    @Test
    public void createOntModel() {
        File script = new File("src/test/resources/scripts_test/sample/sample-script.ttl");
        List<File> mockScripts = new ArrayList<>();
        //TODO parametrize later
        mockScripts.add(script);
        when(scriptDao.getScripts()).thenReturn(mockScripts);

        Model ontModel = ontologyHelper.createOntModel(script);

        Assertions.assertEquals(8, ontModel.size());
    }
}