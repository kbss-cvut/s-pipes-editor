package og_spipes.service;

import og_spipes.persistence.dao.ScriptDAO;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.SQLOutput;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OntologyHelper {

    private static final Logger LOG = LoggerFactory.getLogger(OntologyHelper.class);
    private final ScriptDAO scriptDao;

    @Autowired
    public OntologyHelper(ScriptDAO scriptDao) {
        this.scriptDao = scriptDao;
    }

    public OntModel createOntModel(File file) {
        String fileUri = getOntologyUri(file);
        List<File> scripts = scriptDao.getScripts();

        OntDocumentManager documentManager = new OntDocumentManager();
        documentManager.clearCache();
        documentManager.setReadFailureHandler((s, model, e) -> LOG.info(e.getLocalizedMessage()));
        for(File s : scripts){
            String ontologyUri = getOntologyUri(s);
            String absolutePath = s.getAbsolutePath();
            documentManager.addAltEntry(ontologyUri, absolutePath);
        }
        OntModel model = documentManager.getOntology(fileUri, OntModelSpec.OWL_MEM);
        System.out.println("statements: " + model.listStatements().toList().size());
        /**
         * Problematic part if we share the test context:
         * If we run only ViewControllerTest the "Get graph view" test has statements 4363 (System.out.println("statements: " + model.listStatements().toList().size());)
         * If we run both ScriptControllerTest and ViewControllerTest "Get graph view" test has only statements 48 (System.out.println("statements: " + model.listStatements().toList().size());)
         */
        model.loadImports();

        return model;
    }

    public String getOntologyUri(File f) {
        LOG.debug("Looking for an ontology in file " + f.getName());
        Model defaultModel = ModelFactory.createDefaultModel();
        List<Statement> statements = defaultModel
                .read(f.getAbsolutePath(), org.apache.jena.util.FileUtils.langTurtle)
                .listStatements(null, RDF.type, OWL.Ontology).toList();
        List<String> listURI = statements.stream().map(x -> x.getSubject().getURI())
                .collect(Collectors.toList());

        //TODO handle errors later - such as HttpException etc...

        return listURI.get(0);
    }

}
