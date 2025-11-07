package og_spipes.rest;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @Test
    public void testDownloadOfScript() throws Exception {
        File scriptPath = new File("src/test/resources/scripts_test/sample/hello-world/hello-world2.sms.ttl");
        MvcResult mvcResult = this.mockMvc.perform(get("/file/download")
                .param("file", scriptPath.getAbsolutePath()))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        String res = FileUtils.readFileToString(scriptPath, StandardCharsets.UTF_8);
        Assertions.assertEquals(res, content);
    }

    @Test
    public void testException() throws Exception {
        this.mockMvc.perform(get("/file/download")
                .param("file", "notExistFile"))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

}

