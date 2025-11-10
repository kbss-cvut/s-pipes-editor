package og_spipes.rest;

import com.google.common.collect.Sets;
import og_spipes.config.Constants;
import og_spipes.service.ModuleExecutionInfo;
import og_spipes.service.ViewService;
import og_spipes.testutil.AbstractControllerTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExecutionControllerTest extends AbstractControllerTest {

    @Value(Constants.SCRIPTPATH_SPEL)
    private String scriptPath;

    @Mock
    private ViewService viewService;

    @BeforeEach
    void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPath);
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