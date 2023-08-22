package og_spipes.persistence.dao;

import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import og_spipes.model.spipes.FunctionDTO;
import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ScriptDAOTest {

    private final ScriptDAO scriptDao = new ScriptDAO(new File("src/test/resources/scripts_test/sample/").getAbsolutePath().split(","));
    Model defaultModel = ModelFactory.createDefaultModel().read(
            new File("src/test/resources/scripts_test/sample/simple-import/script.ttl").getAbsolutePath()
    );

    @Test
    @DisplayName("Correct init of entity manager factory")
    public void correctInitOfEntityManagerFactory() {
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("cz.cvut.jopa.scanPackage", "og_spipes.model");
        expectedMap.put(JOPAPersistenceProperties.JPA_PERSISTENCE_PROVIDER, "cz.cvut.kbss.jopa.model.JOPAPersistenceProvider");
        expectedMap.put("cz.cvut.jopa.dataSource.class", "cz.cvut.kbss.ontodriver.jena.JenaDataSource");
        expectedMap.put("cz.cvut.jopa.lang", "en");
        expectedMap.put("in-memory", "true");
        expectedMap.put("cz.cvut.jopa.ontology.logicalUri", "http://temporary");
        expectedMap.put("cz.cvut.jopa.ontology.physicalURI", "local://temporary");
        expectedMap.put("cz.cvut.jopa.ic.validation.disableOnLoad", "true");

        Assertions.assertEquals(
                scriptDao.emf.getProperties(),
                expectedMap,
                "configuration should be as expected");
    }

    @Test
    public void getModulesTypes(){
        List<ModuleType> moduleTypes = scriptDao.getModuleTypes(defaultModel);

        Assertions.assertEquals(77, moduleTypes.size());
    }

    @Test
    public void getModules() {
        List<Module> modules = scriptDao.getModules(defaultModel);

        Assertions.assertEquals(18, modules.size());
    }

    @Test
    public void getScripts() {
        List<File> files = scriptDao.getScripts();

        Assertions.assertEquals(20, files.size());
    }

    @Test
    public void getFunctionStatements() {
        StmtIterator iterator = scriptDao.getFunctionStatements(defaultModel);

        Assertions.assertEquals(3, iterator.toList().size());
    }

    @Test
    public void moduleFunctions() {
        List<FunctionDTO> functionDTOS = scriptDao.moduleFunctions(defaultModel);

        Assertions.assertEquals(3, functionDTOS.size());
    }

    @Test
    public void findScriptByOntologyName() throws IOException {
        File scriptByOntologyName = scriptDao.findScriptByOntologyName("http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.2");

        Assertions.assertEquals("hello-world2.sms.ttl", scriptByOntologyName.getName());
    }

}