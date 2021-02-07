package og_spipes.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FormControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //TODO before every tmp configuration, also create /tmp configuration

    @Test
    @DisplayName("Get parsed s-form")
    public void testGetScriptModuleTypes() throws Exception {
        //TODO inline JsonLD from object

        this.mockMvc.perform(post("/scripts/forms")
                .content(
                        "{" +
                            "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/question-dto\"," +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-type-uri\": \"http://topbraid.org/sparqlmotionlib#ApplyConstruct\"," +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/construct-greeding\"," +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\": \"/home/chlupnoha/IdeaProjects/og-spipes/src/test/resources/scripts_test/sample/hello-world/hello-world.sms.ttl\"" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        //TODO better comparsion
    }

}