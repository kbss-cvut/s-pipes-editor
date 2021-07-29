package og_spipes.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import og_spipes.model.spipes.FunctionDTO;
import og_spipes.service.FormService;
import og_spipes.service.FunctionService;
import og_spipes.service.SPipesExecutionService;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FunctionController.class)
@TestPropertySource(locations="classpath:application.properties")
public class FunctionControllerTest {

    @Value("${scriptPaths}")
    private String scriptPaths;

    @MockBean
    private FunctionService functionService;

    @MockBean
    private SPipesExecutionService executionService;

    @MockBean
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
        mapper.registerModule(new JsonLdModule());
    }

    @Test
    @DisplayName("List script functions")
    public void testGetScriptModuleTypes() throws Exception {
        List<FunctionDTO> functionDTOS = new ArrayList<>();
        functionDTOS.add(new FunctionDTO("functionUri", "execute-greeding", new HashSet<>()));
        org.mockito.Mockito.when(functionService.moduleFunctions(any())).thenReturn(functionDTOS);
        System.out.println(functionService.hashCode());

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

        List<FunctionDTO> obj = mapper.readValue(content, new TypeReference<List<FunctionDTO>>(){});

        Assert.assertEquals(1, obj.size());
        Assert.assertEquals("execute-greeding", obj.get(0).getFunctionLocalName());
    }

    @Test
    @DisplayName("SPipes execution test")
    public void testExecutionService() throws Exception {
        org.mockito.Mockito.when(executionService.serviceExecution(any(), any())).thenReturn("execution started");
        this.mockMvc.perform(post("/function/execute")
                .content(
                        "{" +
                                "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/execution-function-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-function-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/execution-function-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes-view/has-parameter\": \"param=p\"" +
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