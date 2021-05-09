package og_spipes.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import og_spipes.model.Vocabulary;
import og_spipes.model.spipes.*;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static og_spipes.model.Vocabulary.s_c_Modules;

@Repository
public class TransformationDAO extends BaseDAO<TransformationDTO> {

    protected TransformationDAO(@Qualifier("sesameEM") EntityManager em) {
        super(em, TransformationDTO.class);
    }

    public List<TransformationDTO> getAllExecutionTransformation() {
        return em.createNativeQuery("select distinct ?s where { ?s a ?type . ?s ?part ?y . ?y ?input ?z . }", TransformationDTO.class)
                .setParameter("type", URI.create("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation"))
                .setParameter("part", URI.create("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/has-part"))
                .setParameter("input", URI.create("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/has-input"))
                .getResultList();
    }

    //TODO get all executions pagination

}
