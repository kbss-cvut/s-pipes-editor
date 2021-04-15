package og_spipes.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import og_spipes.model.spipes.FunctionDTO;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application.properties")
public class FunctionControllerTest {

    @Value("${repositoryUrl}")
    private String repositoryUrl;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(repositoryUrl);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/hello-world"), scriptsHomeTmp);
        mapper.registerModule(new JsonLdModule());
    }

    @Test
    @DisplayName("List script functions")
    public void testGetScriptModuleTypes() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/function/script")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \"" + repositoryUrl + "/hello-world.sms.ttl\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        System.out.println("CONENTU");
        System.out.println(content);

        List<FunctionDTO> obj = mapper.readValue(content, new TypeReference<List<FunctionDTO>>(){});

        Assert.assertEquals(1, obj.size());
        Assert.assertEquals("execute-greeding", obj.get(0).getFunctionLocalName());
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(repositoryUrl));
    }

}