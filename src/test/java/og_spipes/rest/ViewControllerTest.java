package og_spipes.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get file moduleTypes")
    public void testGetScriptModuleTypes() throws Exception {
        //TODO enforce some assertion
        File scriptPath = new File("src/test/resources/scripts_test/sample/hello-world/hello-world.sms.ttl");
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

}

