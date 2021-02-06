package og_spipes.service;

import og_spipes.model.spipes.ModuleType;
import og_spipes.persistence.dao.ScriptDao;
import org.apache.commons.io.FileUtils;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScriptService {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptService.class);
    private final ScriptDao scriptDao;
    private final OntologyHelper ontologyHelper;

    @Autowired
    public ScriptService(ScriptDao scriptDao, OntologyHelper ontologyHelper) {
        this.scriptDao = scriptDao;
        this.ontologyHelper = ontologyHelper;
    }

    public List<ModuleType> getModuleTypes(String filepath){
        OntModel ontModel = ontologyHelper.createOntModel(new File(filepath));
        return scriptDao.getModuleTypes(ontModel);
    }

}
