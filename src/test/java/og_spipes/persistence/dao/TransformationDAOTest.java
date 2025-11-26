package og_spipes.persistence.dao;

import og_spipes.model.spipes.TransformationDTO;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private Repository repository;

    @BeforeEach
    void beforeEach() throws IOException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.clear();
            conn.add(new File("src/test/resources/rdf4j_source/repositories/rdf4j_export"),
                    null, RDFFormat.TURTLE);
        }
    }

    @Test
    public void getAllExecutionTransformation(){
        List<TransformationDTO> allExecutionTransformation = transformationDAO.getAllExecutionTransformation();
        Assertions.assertEquals(1, allExecutionTransformation.size());
    }
}
