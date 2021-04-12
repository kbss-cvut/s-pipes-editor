package og_spipes.service;

import cz.cvut.sforms.model.Question;
import cz.cvut.spipes.transform.Transformer;
import cz.cvut.spipes.transform.TransformerImpl;
import og_spipes.model.Vocabulary;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.listeners.ChangedListener;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FunctionService {

    private static final Logger LOG = LoggerFactory.getLogger(FunctionService.class);

    private final OntologyHelper helper;

    @Autowired
    public FunctionService(OntologyHelper helper) {
        this.helper = helper;
    }



}
