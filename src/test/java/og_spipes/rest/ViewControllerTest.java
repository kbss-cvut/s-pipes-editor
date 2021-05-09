package og_spipes.rest;

import og_spipes.model.spipes.TransformationDTO;
import og_spipes.persistence.dao.TransformationDAO;
import og_spipes.service.ViewService;
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ViewControllerTest {

    @Value("${repositoryUrl}")
    private String repositoryUrl;

    @Autowired
    private MockMvc mockMvc;

//    private static Repository sesameRepo;
//
//    @BeforeAll
//    public static void beforeAll() throws IOException, InterruptedException {
//        File dataDir = new File("file:/tmp/og_spipes_sesame/repositories/s-pipes-hello-world");
//        sesameRepo = new SailRepository( new NativeStore(dataDir) );
//        sesameRepo.initialize();
//        RepositoryConnection conn = sesameRepo.getConnection();
//        conn.add(new File("/home/jordan/IdeaProjects/s-pipes-newgen/src/test/resources/rdf4j_source/repositories/rdf4j_export"), null, RDFFormat.TURTLE);
//    }

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(repositoryUrl);
        if (scriptsHomeTmp.exists()) {
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
    }

    @Test
    @DisplayName("Get graph view")
    public void testViewOfScript() throws Exception {
        //TODO enforce some assertion
        File scriptPath = new File(repositoryUrl + "/hello-world/hello-world2.sms.ttl");
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


    @Test
    @DisplayName("Get graph view with execution")
    @Disabled //TODO ask how to add data to sesame DB
    public void testViewOfScriptWithExecution() throws Exception {
        //TODO enforce some assertion
        File scriptPath = new File(repositoryUrl + "/hello-world/hello-world2.sms.ttl");
        String transformationId = "http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation/1619043854875003";
        this.mockMvc.perform(post("/views/new")
                .content(
                        "{" +
                                "\"@type\":\"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\":\"" + scriptPath + "\", " +
                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-transformation-id\":\"" + transformationId + "\"" +
                                "}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(repositoryUrl));
    }

}

