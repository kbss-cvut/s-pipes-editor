package og_spipes.service;

import og_spipes.persistence.dao.ScriptDao;
import org.apache.jena.ontology.OntModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OntologyHelperTest {

    @Mock
    private ScriptDao scriptDao;

    @InjectMocks
    private OntologyHelper ontologyHelper;

    @Test
    public void getOntologyUri() {
        //TODO parametrize later
        String res = ontologyHelper.getOntologyUri(new File("src/test/resources/scripts_test/sample/sample-script.ttl"));

        assertEquals("http://www.semanticweb.org/sample-script", res);
    }

    @Test
    public void createOntModel() {
        File script = new File("src/test/resources/scripts_test/sample/sample-script.ttl");
        List<File> mockScripts = new ArrayList<>();
        //TODO parametrize later
        mockScripts.add(script);
        when(scriptDao.getScripts()).thenReturn(mockScripts);

        OntModel ontModel = ontologyHelper.createOntModel(script);

        assertEquals(8, ontModel.size());
    }
}