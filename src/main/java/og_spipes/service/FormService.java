package og_spipes.service;

import cz.cvut.sforms.model.Question;
import cz.cvut.spipes.transform.Transformer;
import cz.cvut.spipes.transform.TransformerImpl;
import og_spipes.model.Vocabulary;
import org.apache.jena.ontology.OntModel;
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
public class FormService {

    private static final Logger LOG = LoggerFactory.getLogger(FormService.class);

    private final OntologyHelper helper;

    private final Transformer transformer = new TransformerImpl();

    @Autowired
    public FormService(OntologyHelper helper) {
        this.helper = helper;
    }

    public Question generateModuleForm(String scriptPath, String moduleUri, String moduleTypeUri){
        LOG.info("Generating form for script " + scriptPath + ", module " + moduleUri + ", moduleType " + moduleTypeUri);
        OntModel ontModel = helper.createOntModel(new File(scriptPath));
        Optional<Statement> moduleType = ontModel.listStatements(
                ontModel.getResource(moduleUri),
                RDF.type,
                ""
        ).filterDrop(
                x -> x.getObject().asResource().getURI().equals(Vocabulary.s_c_Modules)
        ).nextOptional();
        return transformer.script2Form(
                null, //TODO get dummy URL to get it work
                moduleType.map(x -> x.getObject().asResource()).orElse(ontModel.getResource(moduleTypeUri))
        );
    }

    public void mergeFrom(String scriptPath, Question rootQuestion, String moduleType) {
        LOG.info("Generating form for script " + scriptPath + ", moduleType " + moduleType);
        OntModel ontModel = helper.createOntModel(new File(scriptPath));
        Map<String, Model> modelMap = transformer.form2Script(ontModel, rootQuestion, moduleType);
        modelMap.forEach((file, model) -> {
            try {
                FileOutputStream os = new FileOutputStream(scriptPath);
                model.write(os, FileUtils.langTurtle);
            } catch (FileNotFoundException e) {
                LOG.error(e.getMessage());
            }
        });
    }

}
