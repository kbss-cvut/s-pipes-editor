package og_spipes.rest;

import com.google.common.collect.Sets;
import og_spipes.service.ModuleExecutionInfo;
import og_spipes.service.ViewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExecutionControllerTest {

    @Mock
    private ViewService viewService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("List history of execution")
    public void testScriptsEndpoint() throws Exception {
        this.mockMvc.perform(get("/execution/history"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    @DisplayName("Get file moduleTypes")
    public void testGetScriptModuleTypes() throws Exception {
        HashSet<ModuleExecutionInfo> executionInfos = Sets.newHashSet(
                new ModuleExecutionInfo("moduleUri", null, 100L, 122L, 123L)
        );
        Mockito.when(viewService.modulesExecutionInfo("http://example.com/transformationId")).thenReturn(executionInfos);
        this.mockMvc.perform(post("/execution/history-modules")
                .content(
                        "{" +
                            "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-transformation-id\": \"http://example.com/transformationId\"" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}