package og_spipes.persistence.dao;

import og_spipes.model.spipes.TransformationDTO;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TransformationDAOTest {

    @Autowired
    private TransformationDAO transformationDAO;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        File dataDir = tempDir.resolve("repositories").resolve("s-pipes-hello-world").toFile();
        Repository sesameRepo = new SailRepository(new NativeStore(dataDir));
        sesameRepo.init();
        RepositoryConnection conn = sesameRepo.getConnection();
        conn.clear();
        conn.add(new File("src/test/resources/rdf4j_source/repositories/rdf4j_export"), null, RDFFormat.TURTLE);
        conn.close();
        sesameRepo.shutDown();
    }

    @Test
    public void getAllExecutionTransformation(){
        List<TransformationDTO> allExecutionTransformation = transformationDAO.getAllExecutionTransformation();
        Assertions.assertEquals(1, allExecutionTransformation.size());
    }
}
