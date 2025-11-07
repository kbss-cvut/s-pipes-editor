package og_spipes.rest;

import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.*;

import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @Test
    public void testGetReportsMbForFurhterUser() throws Exception {
        //TODO enforce some assertion
        this.mockMvc.perform(get("/reports"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

}