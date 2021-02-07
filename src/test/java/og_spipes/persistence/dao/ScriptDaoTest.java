package og_spipes.persistence.dao;

import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ScriptDaoTest {

    Model defaultModel =  ModelFactory.createDefaultModel().read(
            new File("src/test/resources/scripts_test/sample/simple-import/script.ttl").getAbsolutePath()
    );
    private final ScriptDao scriptDao = new ScriptDao();

    @Test
    public void correctInitOfEntityManagerFactory(){
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("cz.cvut.jopa.scanPackage", "og_spipes.model");
        expectedMap.put("javax.persistence.provider", "cz.cvut.kbss.jopa.model.JOPAPersistenceProvider");
        expectedMap.put("cz.cvut.jopa.dataSource.class", "cz.cvut.kbss.ontodriver.jena.JenaDataSource");
        expectedMap.put("cz.cvut.jopa.lang", "en");
        expectedMap.put("in-memory", "true");
        expectedMap.put("cz.cvut.jopa.ontology.logicalUri", "http://temporary");
        expectedMap.put("cz.cvut.jopa.ontology.physicalURI",  "local://temporary");

        assertEquals(
                "configuration should be as expected",
                scriptDao.emf.getProperties(),
                expectedMap
        );
    }

    @Test
    public void getModulesTypes(){
        List<ModuleType> moduleTypes = scriptDao.getModuleTypes(defaultModel);

        assertEquals(77, moduleTypes.size());
    }

    @Test
    public void getModules() {
        List<Module> modules = scriptDao.getModules(defaultModel);

        assertEquals(18, modules.size());
    }

    @Test
    public void getScripts() {
        List<File> files = scriptDao.getScripts();

        assertEquals(13, files.size());
    }
}