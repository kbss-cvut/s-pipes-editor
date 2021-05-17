package og_spipes.service;

import com.google.common.collect.Sets;
import og_spipes.model.dto.SHACLValidationResultDTO;
import og_spipes.shacl.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.validation.SHACLException;
import org.topbraid.shacl.validation.ValidationReport;
import org.topbraid.shacl.validation.ValidationResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SHACLExecutorService {

    private static final Logger LOG = LoggerFactory.getLogger(SHACLExecutorService.class);

    public Set<SHACLValidationResultDTO> testModel(Set<URL> ruleSet, String scriptPath) throws IOException, URISyntaxException {
        final Model dataModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, null);

        final Set<SHACLValidationResultDTO> res = new HashSet<>();

        try{
            OntDocumentManager.getInstance().setProcessImports(false);
            dataModel.read(new FileInputStream(scriptPath), "urn:dummy", FileUtils.langTurtle);

            final Validator validator = new Validator();
            for(URL url : ruleSet){
                ValidationReport report = validator.validate(dataModel, Sets.newHashSet(url));
                for(ValidationResult result : report.results()){
                    String ruleComment = getRuleComment(new File(url.toURI()).getAbsolutePath());
                    res.add(new SHACLValidationResultDTO(
                            result.getFocusNode().toString(),
                            result.getSeverity().getLocalName(),
                            result.getMessage(),
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

}
