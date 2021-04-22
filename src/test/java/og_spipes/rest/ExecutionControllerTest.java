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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application.properties")
public class ExecutionControllerTest {

    @Value("${repositoryUrl}")
    private String repositoryUrl;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(repositoryUrl);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/hello-world"), scriptsHomeTmp);
    }

    @Test
    @DisplayName("List history of execution")
    public void testScriptsEndpoint() throws Exception {
        this.mockMvc.perform(get("/execution/history"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

    }

//    @Test
//    @DisplayName("Get file moduleTypes")
//    public void testGetScriptModuleTypes() throws Exception {
//        this.mockMvc.perform(post("/execution/history")
//                .content(
//                        "{" +
//                            "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
//                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \""+repositoryUrl+"/hello-world.sms.ttl\"" +
//                        "}"
//                )
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//        //TODO think about assertion approach
//    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(repositoryUrl));
    }

}