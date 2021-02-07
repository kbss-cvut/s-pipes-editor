package og_spipes.service;

import og_spipes.persistence.dao.ScriptDao;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OntologyHelper {

//    private static final Logger LOG = LoggerFactory.getLogger(OntologyHelper.class);
    private final ScriptDao scriptDao;

    @Autowired
    public OntologyHelper(ScriptDao scriptDao) {
        this.scriptDao = scriptDao;
    }

    public OntModel createOntModel(File file) {
        String fileUri = getOntologyUri(file);
        List<File> scripts = scriptDao.getScripts();

        OntDocumentManager documentManager = OntDocumentManager.getInstance();
        documentManager.clearCache();
        documentManager.setReadFailureHandler((s, model, e) -> {
//            LOG.info(e.getLocalizedMessage());
            System.out.println(e.getLocalizedMessage());
        });
        scripts.forEach(s -> {
            String ontologyUri = getOntologyUri(s);
            String absolutePath = s.getAbsolutePath();
            documentManager.addAltEntry(ontologyUri, absolutePath);
        });
        OntModel model = documentManager.getOntology(fileUri, OntModelSpec.OWL_MEM);
        model.loadImports();

        return model;
    }

//    public Map<String, File> collectOntologyUris(List<File> files) {
//        HashMap<String, File> stringFileHashMap = new HashMap<>();
//        files.forEach(f -> stringFileHashMap.put(getOntologyUri(f), f));
//        return stringFileHashMap;
//    }

//    public OntDocumentManager getOntDocumentManager(List<File> scripts){
//        OntDocumentManager documentManager = OntDocumentManager.getInstance();
//        documentManager.clearCache();
//        documentManager.setReadFailureHandler((s, model, e) -> {
//            LOG.info(e.getLocalizedMessage());
//        });
//        scripts.forEach(s -> {
//            String ontologyUri = getOntologyUri(s);
//            String absolutePath = s.getAbsolutePath();
//            documentManager.addAltEntry(ontologyUri, absolutePath);
//        });
//        return documentManager;
//    }

    public String getOntologyUri(File f) {
//        LOG.debug("Looking for an ontology in file " + f.getName());
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
