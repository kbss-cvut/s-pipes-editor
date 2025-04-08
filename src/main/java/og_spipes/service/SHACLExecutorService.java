package og_spipes.service;

import com.google.common.collect.Sets;
import og_spipes.config.Constants;
import og_spipes.model.dto.SHACLValidationResultDTO;
import og_spipes.service.util.ScriptImportGroup;
import og_spipes.shacl.Validator;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.validation.ValidationReport;
import org.topbraid.shacl.validation.ValidationResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SHACLExecutorService {

    private static final Logger LOG = LoggerFactory.getLogger(SHACLExecutorService.class);

    private final String[] scriptPaths;

    public SHACLExecutorService(@Value(Constants.SCRIPTPATH_SPEL) String[] scriptPaths) {
        this.scriptPaths = scriptPaths;
    }

    public Set<SHACLValidationResultDTO> testModel(Set<URL> ruleSet, String rootScript) throws IOException, URISyntaxException {
        final Model dataModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, null);
        ScriptImportGroup importGroup = new ScriptImportGroup(scriptPaths, new File(rootScript));

        final Set<SHACLValidationResultDTO> res = new HashSet<>();

        try{
            OntDocumentManager.getInstance().setProcessImports(false);
            OntDocumentManager.getInstance().setReadFailureHandler((s, model, e) -> LOG.debug(s + "; " +e.getLocalizedMessage()));
            for(File f : importGroup.getUsedFiles()){
                try(InputStream is = new FileInputStream(f)) {
                    dataModel.read(is, "urn:dummy", FileUtils.langTurtle);
                }
            }

            final Validator validator = new Validator();
            for(URL url : ruleSet){
                ValidationReport report = validator.validate(dataModel, Sets.newHashSet(url));
                for(ValidationResult result : report.results()){
                    String ruleComment = getRuleComment(new File(url.toURI()).getAbsolutePath());
                    res.add(new SHACLValidationResultDTO(
                            result.getFocusNode().toString(),
                            result.getSeverity().getLocalName(),
                            result.getMessage().replace("@en", ""),
                            url.toString(),
                            ruleComment
                    ));
                }
            }
        }finally {
            dataModel.removeAll();
            OntDocumentManager.getInstance().setProcessImports(true);
        }

        return res;
    }

    private String getRuleComment(String rulePath) {
        Model defaultModel = ModelFactory.createDefaultModel();
        List<Statement> comments = defaultModel
                .read(rulePath, org.apache.jena.util.FileUtils.langTurtle)
                .listStatements(null, new PropertyImpl("http://www.w3.org/2000/01/rdf-schema#comment"), (RDFNode) null)
                .toList();

        List<String> comment = comments.stream().map(x -> x.getObject().toString())
                .collect(Collectors.toList());

        if(comment.size() > 0){
            String res = comment.get(0);
            if (comment.size() > 1) LOG.info("More comments in rule found! Firstly one is used.");
            return res;
        }

        return "";
    }

    public Set<SHACLValidationResultDTO> testModel(List<File> rules, String rootScript) throws IOException, URISyntaxException {
        File rootScriptFile = new File(rootScript);

        Set<SHACLValidationResultDTO> violations = new HashSet<>();
        if(! shouldTest(rules, rootScriptFile))
            return violations;
        long timestamp = System.currentTimeMillis();
        try(ValidationContext context = createValidationContext(rootScript)){
            for(File f : rules){
                violations.addAll(context.testModel(Collections.singleton(f.toURI().toURL())));
            }
        }

        cacheValidationResult(rootScriptFile, violations.isEmpty(), timestamp);
        return violations;
    }

    protected Map<File, VR> validationCache = new HashMap<>();

    protected void cacheValidationResult(File file, boolean valid, long timestamp){
        validationCache.put(file, new VR(timestamp, valid));
    }

    protected boolean shouldTest(List<File> rules, File rootScriptFile){
        VR v = validationCache.get(rootScriptFile);
        if(v == null)
            return true;
        boolean rootScriptFileOutdated = og_spipes.utils.FileUtils.isOutdated(rootScriptFile, v.timestamp);
        boolean rulesOutdated = rules.stream().filter(rf -> og_spipes.utils.FileUtils.isOutdated(rf, v.timestamp))
                .findAny().isPresent();
        return rootScriptFileOutdated || rulesOutdated || !v.getValid();
    }

    protected ValidationContext createValidationContext(String rootScript) throws IOException {
        final Model dataModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, null);
        ScriptImportGroup importGroup = new ScriptImportGroup(scriptPaths, new File(rootScript));

        OntDocumentManager.getInstance().setProcessImports(false);
        OntDocumentManager.getInstance().setReadFailureHandler((s, model, e) -> LOG.debug(s + "; " +e.getLocalizedMessage()));
        for(File f : importGroup.getUsedFiles()){
            try(InputStream is = new FileInputStream(f)) {
                dataModel.read(is, "urn:dummy", FileUtils.langTurtle);
            }
        }
        return new ValidationContext(dataModel);
    }

    protected class ValidationContext implements AutoCloseable{
        protected Model dataModel;

        public ValidationContext() {
        }

        public ValidationContext(Model dataModel) {
            this.dataModel = dataModel;
        }

        public Model getDataModel() {
            return dataModel;
        }

        public void setDataModel(Model dataModel) {
            this.dataModel = dataModel;
        }

        public Set<SHACLValidationResultDTO> testModel(Set<URL> ruleSet) throws IOException, URISyntaxException {
            final Set<SHACLValidationResultDTO> res = new HashSet<>();
            final Validator validator = new Validator();
            for(URL url : ruleSet){
                ValidationReport report = validator.validate(dataModel, Sets.newHashSet(url));
                for(ValidationResult result : report.results()){
                    String ruleComment = getRuleComment(new File(url.toURI()).getAbsolutePath());
                    res.add(new SHACLValidationResultDTO(
                            result.getFocusNode().toString(),
                            result.getSeverity().getLocalName(),
                            result.getMessage().replace("@en", ""),
                            url.toString(),
                            ruleComment
                    ));
                }
            }
            return res;
        }

        @Override
        public void close() {
            dataModel.removeAll();
            OntDocumentManager.getInstance().setProcessImports(true);
        }
    }


    class VR {
        protected Long timestamp;
        protected Boolean valid;

        public VR(Long timestamp, Boolean valid) {
            this.timestamp = timestamp;
            this.valid = valid;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public Boolean getValid() {
            return valid;
        }
    }
}
