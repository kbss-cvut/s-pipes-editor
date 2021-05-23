package og_spipes.service;

import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.persistence.dao.ScriptDAO;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
public class ScriptServiceTest {

    @Autowired
    private ScriptService scriptService;

    @Value("${scriptPaths}")
    private String scriptPaths;

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPaths);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
    }

    @Test
    public void getModuleTypes() {
        List<ModuleType> moduleTypes = scriptService.getModuleTypes(scriptPaths + "/hello-world/hello-world.sms.ttl");

        Assertions.assertEquals(25, moduleTypes.size());
    }

    @Test
    public void getModule() {
        List<Module> modules = scriptService.getModules(scriptPaths + "/hello-world/hello-world.sms.ttl");

        Assertions.assertEquals(3, modules.size());
    }

    @Test
    public void moveModule() throws FileNotFoundException {
        scriptService.moveModule(
                scriptPaths + "/hello-world/hello-world.sms.ttl",
                scriptPaths + "/hello-world/hello-world2.sms.ttl",
                "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/metadata/"
        );

    }

//    @AfterEach
//    public void after() {
//        FileSystemUtils.deleteRecursively(new File(scriptPaths));
//    }

}