package og_spipes.service;

import cz.cvut.sforms.model.Question;
import cz.cvut.spipes.transform.Transformer;
import cz.cvut.spipes.util.JenaUtils;
import og_spipes.model.Vocabulary;
import og_spipes.persistence.dao.OntologyDao;
import og_spipes.service.util.TransformerImpl;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;

@Service
public class FormService {

    private static final Logger LOG = LoggerFactory.getLogger(FormService.class);

    private final OntologyHelper helper;

//    private final Transformer transformer = new TransformerImpl();

    private final Transformer ownTransformer = new TransformerImpl();

    @Autowired
    public FormService(OntologyHelper helper) {
        this.helper = helper;
    }

    public Question generateModuleForm(String scriptPath, String moduleUri, String moduleTypeUri){
        LOG.info("Generating form for script " + scriptPath + ", module " + moduleUri + ", moduleType " + moduleTypeUri);
        Model ontModel = helper.createOntModel(new File(scriptPath));
        Optional<Statement> moduleType = ontModel.listStatements(
                ontModel.getResource(moduleUri),
                RDF.type,
                ""
        ).filterDrop(
                x -> x.getObject().asResource().getURI().equals(Vocabulary.s_c_Modules)
        ).nextOptional();

        Resource mType = moduleType.map(x -> x.getObject().asResource()).orElse(ontModel.getResource(moduleTypeUri));
        mType.listProperties().forEachRemaining(x -> LOG.info(x.toString()));

        return ownTransformer.script2Form(
                resolveURI(ontModel, moduleUri, scriptPath),
                mType
        );
    }

    public void mergeFrom(String scriptPath, Question rootQuestion, String moduleType) {
        LOG.info("Generating form for script " + scriptPath + ", moduleType " + moduleType);
        Model ontModel = helper.createOntModel(new File(scriptPath));
        Map<String, Model> modelMap = ownTransformer.form2Script(ontModel, rootQuestion, moduleType);
        modelMap.forEach((file, model) -> {
            try {
                JenaUtils.writeScript(Path.of(scriptPath), getBaseModel(model));
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        });
    }

    private Model getBaseModel(Model model){
        if (model instanceof OntModel) {
            return ((OntModel) model).getBaseModel();
        }
        return model;
    }

    private Resource resolveURI(Model model, String moduleUri, String scriptPath){
        Resource origin = model.getResource(moduleUri);
        Random rand = new Random();
        if(origin.getURI() == null){
            String baseURI = OntologyDao.getOntologyUri(new File(scriptPath));
            URI dummyURI = URI.create(baseURI + "/change_" + rand.nextInt(Integer.MAX_VALUE));
            Model defaultModel = ModelFactory.createDefaultModel();
            return defaultModel.createResource(dummyURI.toString());
        }
        return origin;
    }

    public Question generateFunctionForm(String scriptPath, String functionUri){
        LOG.info("Generating form for script " + scriptPath + ", functionUri " + functionUri);
        //TODO imports
        Model defaultModel = ModelFactory.createDefaultModel();
        Model ontModel = defaultModel
                .read(scriptPath, org.apache.jena.util.FileUtils.langTurtle);
        return ownTransformer.functionToForm(
                ontModel,
                ontModel.getResource(functionUri)
        );
    }

}
