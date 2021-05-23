package og_spipes.persistence.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import og_spipes.model.spipes.TransformationDTO;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class TransformationDAOTest {

    @Autowired
    private TransformationDAO transformationDAO;

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * BeforeEach is not possible because JOPA lock the repository: SAIL is already locked. It means parametrization via
     * Spring $Value is not possible.
     * @throws IOException
     */
    @BeforeAll
    public static void beforeAll() throws IOException {
        File dataDir = new File("/tmp/og_spipes_sesame/repositories/s-pipes-hello-world");
        Repository sesameRepo = new SailRepository(new NativeStore(dataDir));
        sesameRepo.init();
        RepositoryConnection conn = sesameRepo.getConnection();
        conn.clear();
        conn.add(new File("src/test/resources/rdf4j_source/repositories/rdf4j_export"), null, RDFFormat.TURTLE);
        conn.close();
        sesameRepo.shutDown();
        mapper.registerModule(new JsonLdModule());
    }

    @Test
    public void getAllExecutionTransformation(){
        List<TransformationDTO> allExecutionTransformation = transformationDAO.getAllExecutionTransformation();

        Assertions.assertEquals(1, allExecutionTransformation.size());
    }

    @AfterAll
    public static void afterAll(){
        FileSystemUtils.deleteRecursively(new File("/tmp/og_spipes_sesame/repositories/s-pipes-hello-world"));
    }

}