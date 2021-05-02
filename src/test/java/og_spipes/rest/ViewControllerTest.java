package og_spipes.rest;

import og_spipes.service.ViewService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ViewControllerTest {

    @Value("${repositoryUrl}")
    private String repositoryUrl;

    @Autowired
    private MockMvc mockMvc;

    /**
     * This test requires whole sample folder because it contains necessary import dependencies.
     *
     * @throws Exception
     */
    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(repositoryUrl);
        if (scriptsHomeTmp.exists()) {
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
    }

    @Test
    @DisplayName("Get graph view")
    public void testViewOfScript() throws Exception {
        //TODO enforce some assertion
        File scriptPath = new File(repositoryUrl + "/hello-world/hello-world2.sms.ttl");
        this.mockMvc.perform(post("/views/new")
                .content(
                        "{" +
                                "\"@type\":\"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\":\"" + scriptPath + "\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Get graph view with execution")
    public void testViewOfScriptWithExecution() throws Exception {
        //TODO enforce some assertion
        File scriptPath = new File(repositoryUrl + "/hello-world/hello-world2.sms.ttl");
        //TODO use testing repository - RDF4J export
        String transformationId = "http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation/1619043854875003";
        this.mockMvc.perform(post("/views/new")
                .content(
                        "{" +
                                "\"@type\":\"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\":\"" + scriptPath + "\", " +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-transformation-id\":\"" + transformationId + "\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(repositoryUrl));
    }

}

