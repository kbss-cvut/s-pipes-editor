package og_spipes.service;

import og_spipes.persistence.dao.ScriptDAO;
import org.apache.jena.ontology.OntModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OntologyHelperTest {

    @Mock
    private ScriptDAO scriptDao;

    @InjectMocks
    private OntologyHelper ontologyHelper;

    @Test
    public void getOntologyUri() {
        //TODO parametrize later
        String res = ontologyHelper.getOntologyUri(new File("src/test/resources/scripts_test/sample/sample-script.ttl"));

        Assertions.assertEquals("http://www.semanticweb.org/sample-script", res);
    }

    @Test
    public void createOntModel() {
        File script = new File("src/test/resources/scripts_test/sample/sample-script.ttl");
        List<File> mockScripts = new ArrayList<>();
        //TODO parametrize later
        mockScripts.add(script);
        when(scriptDao.getScripts()).thenReturn(mockScripts);

        OntModel ontModel = ontologyHelper.createOntModel(script);

        Assertions.assertEquals(8, ontModel.size());
    }
}