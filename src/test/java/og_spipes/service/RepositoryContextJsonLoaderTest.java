package og_spipes.service;

import og_spipes.service.exception.ContextNotExistsException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@SpringBootTest
class RepositoryContextJsonLoaderTest {

    @Autowired
    private RepositoryContextJsonLoader repositoryContextJsonLoader;

    @Autowired
    private Repository sesameRepo;

    @Test
    public void test() throws JSONException, IOException {
        RepositoryConnection conn = sesameRepo.getConnection();
        conn.add(new File("src/test/resources/rdf4j_source/repositories/rdf4j_export"), null, RDFFormat.TURTLE);

        //TODO import data not working :/ fix and creat test

        String uri = "file:/home/jordan/apache-tomcat-9.0.45/temp/2021-04-21T22_23_58.900Z-s-pipes-log-5181096301762785817/pipeline-execution-1619043854875003/2021-04-21T22_24_14.949Z-module-1619043854875003-845999858-399429203-input-binding.ttl";

        String contextAsJson = repositoryContextJsonLoader.contextAsJson(
                URI.create(uri)
        );

        Assertions.assertEquals("{}", contextAsJson);
    }

}