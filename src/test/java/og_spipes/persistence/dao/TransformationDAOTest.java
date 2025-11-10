package og_spipes.persistence.dao;

import og_spipes.model.spipes.TransformationDTO;
import og_spipes.testutil.AbstractSpringTest;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.List;

class TransformationDAOTest extends AbstractSpringTest {

    @Autowired
    private TransformationDAO transformationDAO;

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
