package og_spipes.rest;

import og_spipes.testutil.AbstractControllerTest;
import org.junit.jupiter.api.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportControllerTest extends AbstractControllerTest {

    @Test
    public void testGetReportsMbForFurhterUser() throws Exception {
        //TODO enforce some assertion
        this.mockMvc.perform(get("/reports"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

}