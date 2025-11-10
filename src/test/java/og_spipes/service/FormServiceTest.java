package og_spipes.service;

import cz.cvut.sforms.model.Question;
import og_spipes.config.Constants;
import og_spipes.testutil.AbstractSpringTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;


//TODO more robust tests
public class FormServiceTest extends AbstractSpringTest {

    @Autowired
    private FormService formService;

    @Value(Constants.SCRIPTPATH_SPEL)
    private String scriptPaths;

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPaths);
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
                "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/execute-greeting"
        );

        Assertions.assertEquals(1, question.getSubQuestions().size());
    }

}