package og_spipes.rest;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.spipes.model.Transformation;
import og_spipes.model.Vocabulary;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.spipes.TransformationDTO;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ZkouskaControllerTest {

    @Autowired
    @Qualifier("sesameEMF")
    private EntityManagerFactory emf;

    @Test
    @DisplayName("Get file moduleTypes")
    public void testGetScriptModuleTypes() throws Exception {

        EntityManager entityManager = emf.createEntityManager();
        System.out.println(entityManager);

        TransformationDTO transformation = entityManager.find(TransformationDTO.class, URI.create("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation/1618600713528001"));
        System.out.println(transformation.getId());

        List<TransformationDTO> transformations = entityManager.createNativeQuery("select ?s where { ?s a ?type . }", TransformationDTO.class)
                .setParameter("type", URI.create("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation")).getResultList();

        System.out.println(transformations.size());

//        File scriptPath = new File(repositoryUrl + "/hello-world/hello-world.sms.ttl");
//        this.mockMvc.perform(post("/views/new")
//                .content(
//                        "{" +
//                                "\"@type\":\"http://onto.fel.cvut.cz/ontologies/s-pipes/script-dto\"," +
//                                "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\":\"" + scriptPath + "\"" +
//                                "}"
//                )
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
    }

}

