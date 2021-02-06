package og_spipes.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class ScriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("List script folder")
    public void testScriptsEndpoint() throws Exception {
        //TODO enforce some assertion
        this.mockMvc.perform(get("/scripts"))
                .andDo(print());
    }

    @Test
    @DisplayName("Get file moduleTypes")
    public void testGetScriptModuleTypes() throws Exception {
        //TODO enforce some assertion
        this.mockMvc.perform(get("/scripts/moduleTypes"))
                .andDo(print());
    }

}