package og_spipes.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.sforms.model.Question;
import og_spipes.config.Constants;
import og_spipes.config.RestConfig;
import og_spipes.model.spipes.FunctionDTO;
import og_spipes.service.FormService;
import og_spipes.service.FunctionService;
import og_spipes.service.SPipesExecutionService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FunctionController.class)
public class FunctionControllerTest {

    @Value(Constants.SCRIPTPATH_SPEL)
    private String scriptPaths;

    @MockitoBean
    private FunctionService functionService;

    @MockitoBean
    private SPipesExecutionService executionService;

    @MockitoBean
    private FormService formService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPaths);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/hello-world"), scriptsHomeTmp);
        RestConfig.configureObjectMapper(mapper);
    }

    @Test
    @DisplayName("List script functions")
    public void testGetScriptModuleTypes() throws Exception {
        List<FunctionDTO> functionDTOS = new ArrayList<>();
        functionDTOS.add(new FunctionDTO("functionUri", "execute-greeting", new HashSet<>()));
        when(functionService.moduleFunctions(any())).thenReturn(functionDTOS);

        MvcResult mvcResult = this.mockMvc.perform(post("/function/script")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-absolute-path\": \"" + scriptPaths + "/hello-world.sms.ttl\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        JsonNode rootNode = mapper.readTree(content);
        JsonNode listNode = rootNode.get("@list");

        List<FunctionDTO> obj = mapper.readValue(listNode.toString(), new TypeReference<List<FunctionDTO>>(){});

        Assertions.assertEquals(1, obj.size());
        Assertions.assertEquals("execute-greeting", obj.get(0).getFunctionLocalName());
    }

    @Test
    public void testGenerateFunctionForm() throws Exception {
        when(formService.generateFunctionForm(
                scriptPaths + "/hello-world.sms.ttl",
                "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/express-greeting_Return")
        ).thenReturn(new Question());
        this.mockMvc.perform(post("/function/form")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/execution-function-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\": \"" + scriptPaths + "/hello-world.sms.ttl\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-function-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/express-greeting_Return\"" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("SPipes execution function")
    public void testExecutionService() throws Exception {
        this.mockMvc.perform(post("/function/execute")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/execution-function-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-function-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/execute-greeting\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes-view/has-parameter\": \"param=p1\"" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("SPipes module execution test")
    public void testExecutionModule() throws Exception {
        String script = scriptPaths + "/hello-world.sms.ttl";
        this.mockMvc.perform(post("/function/module/execute")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/execution-module-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\": \""+script+"\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/express-greeting_Return\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes-view/has-input-parameter\": \"\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes-view/has-parameter\": \"\"" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(scriptPaths));
    }

}