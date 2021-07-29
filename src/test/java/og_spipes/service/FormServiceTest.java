package og_spipes.service;

import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import cz.cvut.sforms.model.Question;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;

import javax.xml.bind.SchemaOutputResolver;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//TODO more robust tests
@SpringBootTest
public class FormServiceTest {

    @Autowired
    private FormService formService;

    @Value("${scriptPaths}")
    private String scriptPaths;

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPaths);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/hello-world"), scriptsHomeTmp);
    }

//    @Test
//    public void generateModuleForm() {
//        when(formService.generateModuleForm("", "", "")).thenReturn(null);
//
//        Question question = formService.generateModuleForm("", "", "");
//
//        Assertions.assertEquals(null, question);
//    }
//
//    @Test
//    public void mergeFrom() {
//        formService.mergeFrom("", null, "");
//    }

    @Test
    public void functionToForm() {
        Question question = formService.generateFunctionForm(
                scriptPaths + "/hello-world.sms.ttl",
                "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/execute-greeding"
        );

        Assertions.assertEquals(1, question.getSubQuestions().size());
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(scriptPaths));
    }

}