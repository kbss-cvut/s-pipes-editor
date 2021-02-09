package og_spipes.rest;

import og_spipes.model.Vocabulary;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application.properties")
public class ScriptControllerTest {

    @Value("${repositoryUrl}")
    private String repositoryUrl;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(repositoryUrl);
        System.out.println(scriptsHomeTmp);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/hello-world"), scriptsHomeTmp);
    }

    @Test
    @DisplayName("List script folder")
    public void testScriptsEndpoint() throws Exception {
        this.mockMvc.perform(get("/scripts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"children\":[{\"file\":\""+repositoryUrl+"/hello-world.sms.ttl\",\"name\":\"hello-world.sms.ttl\"}],\"name\":\"og_spipes\"}"));
    }

    @Test
    @DisplayName("Get file moduleTypes")
    public void testGetScriptModuleTypes() throws Exception {
        this.mockMvc.perform(post("/scripts/moduleTypes")
                .content(
                        "{" +
                            "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \""+repositoryUrl+"/hello-world.sms.ttl\"" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        //TODO think about assertion approach
    }

    @Test
    @DisplayName("Add dependency between two modules in script")
    public void testAddModuleDependency() throws Exception {
        //TODO try inline JsonLD from object
        String tmpScripts = repositoryUrl + "/hello-world.sms.ttl";
        this.mockMvc.perform(post("/scripts/modules/dependency")
                .content(
                        "{\n" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/dependency-dto\",\n" +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \"" + tmpScripts + "\",\n" +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/express-greeding_Return\",\n" +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-target-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname\"\n" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(204));

        Model modelProject = ModelFactory.createDefaultModel().read("src/test/resources/scripts_test/sample/hello-world/hello-world.sms.ttl");
        Model afterModel = ModelFactory.createDefaultModel().read(tmpScripts);
        long beforeExecutionCount = modelProject.listSubjectsWithProperty(new PropertyImpl(Vocabulary.s_p_next)).toList().stream()
                .map(x -> x.getProperty(new PropertyImpl(Vocabulary.s_p_next))).count();
        long afterExecutionCount = afterModel.listSubjectsWithProperty(new PropertyImpl(Vocabulary.s_p_next)).toList().stream()
                .map(x -> x.getProperty(new PropertyImpl(Vocabulary.s_p_next))).count();
        Assert.assertEquals("Before execution count is 2", 2, beforeExecutionCount);
        Assert.assertEquals("After execution count is 3", 3, afterExecutionCount);
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(repositoryUrl));
    }

}