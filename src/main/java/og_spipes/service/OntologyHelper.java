package og_spipes.service;

import og_spipes.persistence.dao.OntologyDao;
import og_spipes.persistence.dao.ScriptDAO;
import org.apache.jena.graph.impl.SimpleGraphMaker;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.ProfileRegistry;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ModelMakerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class OntologyHelper {

    private static final Logger LOG = LoggerFactory.getLogger(OntologyHelper.class);
    private final ScriptDAO scriptDao;

    @Autowired
    public OntologyHelper(ScriptDAO scriptDao) {
        this.scriptDao = scriptDao;
    }

    /**
     * Create new instance of OntManager, instead of using the shared one. Load all
     * of the scripts and get ontology based on file @param. It is important to mention
     * the method does not use OntDocumentManager.getInstance(), which has to clear data
     * or take care of the synchronization. Another problematic part is to avoid usage of the
     * OntModelSpec such as OWL_MEM, which do not work properly with the imported files.
     * The OntModelSpec caches imported ontologies and it impossible to purge them.
     * @param file - file with OWL.Ontology
     * @return - Ontology with all of the loaded imports
     */
    public Model createOntModel(File file) {
        String fileUri = OntologyDao.getOntologyUri(file);
        List<File> scripts = scriptDao.getScripts();

        OntDocumentManager documentManager = new OntDocumentManager();
        documentManager.setReadFailureHandler((s, model, e) -> LOG.debug(s + "; " +e.getLocalizedMessage()));
        for(File s : scripts){
            String ontologyUri = OntologyDao.getOntologyUri(s);
            if(!ontologyUri.isEmpty()){
                String absolutePath = s.getAbsolutePath();
                documentManager.addAltEntry(ontologyUri, absolutePath);
            }
        }
        ModelMakerImpl modelMaker = new ModelMakerImpl(new SimpleGraphMaker());
        OntModelSpec ontModelSpec = new OntModelSpec(modelMaker, null, null, ProfileRegistry.OWL_LANG);
        return documentManager.getOntology(fileUri, ontModelSpec);
    }

    public static List<Statement> getAllStatementsRecursively(Model fromModel, String moduleURI) {
        Queue<RDFNode> queueA = new LinkedList<>();
        Set<String> traversedNodes = new HashSet<>();

        List<Statement> moduleStatements = fromModel
                .listStatements(fromModel.getResource(moduleURI), null, (RDFNode) null).toList();

        for(Statement st : moduleStatements){
            RDFNode object = st.getObject();
            if(object.isAnon()){
                if(!traversedNodes.contains(object.toString())){
                    traversedNodes.add(object.toString());
                    queueA.add(object);
                }
            }
        }

        while (!queueA.isEmpty()){
            RDFNode anonymousNode = queueA.poll();
            List<Statement> ms = fromModel
                    .listStatements(anonymousNode.asResource(), null, (RDFNode) null).toList();

            for(Statement st : ms){
                RDFNode object = st.getObject();
                if(object.isAnon()){
                    if(!traversedNodes.contains(object.toString())){
                        traversedNodes.add(object.toString());
                        queueA.add(object);
                    }
                }
            }

            moduleStatements.addAll(ms);
        }

        return moduleStatements;
    }


}
