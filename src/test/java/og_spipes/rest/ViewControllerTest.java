package og_spipes.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import og_spipes.config.Constants;
import og_spipes.model.view.View;
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "rdf4j.repositoryUrl=/tmp/og_spipes_sesame/repositories/view-controller/"
})
@AutoConfigureMockMvc
public class ViewControllerTest {

    @Value(Constants.SCRIPTPATH_SPEL)
    private String scriptPaths;

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void beforeAll() throws IOException {
        File dataDir = new File("/tmp/og_spipes_sesame/repositories/view-controller/s-pipes-hello-world");
        Repository sesameRepo = new SailRepository(new NativeStore(dataDir));
        sesameRepo.init();
        RepositoryConnection conn = sesameRepo.getConnection();
        conn.clear();
        conn.add(new File("src/test/resources/rdf4j_source/repositories/rdf4j_export"), null, RDFFormat.TURTLE);
        conn.close();
        sesameRepo.shutDown();
        mapper.registerModule(new JsonLdModule());
    }

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPaths);
        if (scriptsHomeTmp.exists()) {
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
    }

    @Test
    @DisplayName("Get graph view")
    public void testViewOfScript() throws Exception {
        File scriptPath = new File(scriptPaths + "/hello-world/hello-world2.sms.ttl");
        MvcResult mvcResult = this.mockMvc.perform(post("/views/new")
                .content(
                        "{" +
                                "\"@type\":\"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\":\"" +
                                scriptPath.toURI().getPath() + "\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        View view = mapper.readValue(content, View.class);

        Assertions.assertNotNull(view);
    }

    @Test
    @DisplayName("Get graph view with execution")
    public void testViewOfScriptWithExecution() throws Exception {
        File scriptPath = new File(scriptPaths + "/hello-world/hello-world2.sms.ttl");
        String transformationId = "http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation/1619043854875003";
        MvcResult mvcResult = this.mockMvc.perform(post("/views/new")
                .content(
                        "{" +
                                "\"@type\":\"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\":\"" +
                                scriptPath.toURI().getPath() + "\", " +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-transformation-id\":\"" + transformationId + "\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        View view = mapper.readValue(content, View.class);

        Assertions.assertNotNull(view);
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(scriptPaths));
    }

    @AfterAll
    public static void afterAll(){
        FileSystemUtils.deleteRecursively(new File("/tmp/og_spipes_sesame/repositories/view-controller/"));
    }

}

