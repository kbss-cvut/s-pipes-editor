package og_spipes.persistence.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.jsonld.jackson.JsonLdModule;
import og_spipes.model.spipes.TransformationDTO;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class TransformationDAOTest {
    @Autowired
    private TransformationDAO transformationDAO;

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void beforeAll() throws IOException {
        File dataDir = new File("file:/tmp/og_spipes_sesame/repositories/s-pipes-hello-world");
        Repository sesameRepo = new SailRepository(new NativeStore(dataDir));
        sesameRepo.initialize();
        RepositoryConnection conn = sesameRepo.getConnection();
        conn.add(new File("src/test/resources/rdf4j_source/repositories/rdf4j_export"), null, RDFFormat.TURTLE);
        mapper.registerModule(new JsonLdModule());
    }

    /**
     * Working with remote URL http://localhost:8080/rdf4j-server/repositories but as we discussed the local import of the data
     * do not work. Problematic part is to import data via new SailRepository(new NativeStore(dataDir)) to file:/tmp/og_spipes_sesame/repositories/
     * I guess data d not appear inside database.
     */
    @Test
    public void getAllExecutionTransformation(){
        List<TransformationDTO> allExecutionTransformation = transformationDAO.getAllExecutionTransformation();

        Assertions.assertEquals(2, allExecutionTransformation.size());
    }
}