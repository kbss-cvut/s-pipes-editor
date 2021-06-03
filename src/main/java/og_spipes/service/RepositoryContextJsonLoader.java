package og_spipes.service;

//import org.eclipse.rdf4j.rio.rdfjson.RDFJSONWriter;
import og_spipes.service.exception.ContextNotExistsException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.rdfjson.RDFJSONWriter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoryContextJsonLoader {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(RepositoryContextJsonLoader.class);

    private final Repository repository;

    @Autowired
    public RepositoryContextJsonLoader(Repository repository) {
        this.repository = repository;
    }

    public String contextAsJson(URI contextUri) {
        List<Resource> contextIDs = repository.getConnection().getContextIDs().asList();
        Optional<Resource> resource = contextIDs.stream().filter(r -> r.toString().equals(contextUri.toString())).findAny();

        if(!resource.isPresent()){
            LOG.warn("RDF4J CONTEXT IS EMPTY.");
            return "{}";
//            throw new ContextNotExistsException("Resource does not exists");
        }

        RepositoryConnection connection = repository.getConnection();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        RDFHandler rdfHandler = new RDFJSONWriter(bos, RDFFormat.JSONLD);

        connection.export(rdfHandler, resource.get());
        connection.close();

        return bos.toString();
    }

}