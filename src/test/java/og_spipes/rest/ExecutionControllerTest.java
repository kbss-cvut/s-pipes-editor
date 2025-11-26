package og_spipes.rest;

import com.google.common.collect.Sets;
import og_spipes.config.Constants;
import og_spipes.service.ModuleExecutionInfo;
import og_spipes.service.ViewService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ExecutionControllerTest {

    @Value(Constants.SCRIPTPATH_SPEL)
    private String scriptPaths;

    @Mock
    private ViewService viewService;

    @Autowired
    private MockMvc mockMvc;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPaths);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/hello-world"), scriptsHomeTmp);
    }

    @Test
    public void historyOfAllExecutionReturnsJson() throws Exception {
        this.mockMvc.perform(get("/execution/history"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void historyOfModuleReturnsSuccessfully() throws Exception {
        HashSet<ModuleExecutionInfo> executionInfos = Sets.newHashSet(
                new ModuleExecutionInfo("moduleUri", null, 100L, 122L, 123L)
        );
        Mockito.when(viewService.modulesExecutionInfo("http://example.com/transformationId")).thenReturn(executionInfos);
        this.mockMvc.perform(post("/execution/history-modules")
                        .content("""
                        {
                            "@type": "http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto",
                            "http://onto.fel.cvut.cz/ontologies/s-pipes/has-transformation-id": "http://example.com/transformationId"
                        }
                    """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(scriptPaths));
    }

}