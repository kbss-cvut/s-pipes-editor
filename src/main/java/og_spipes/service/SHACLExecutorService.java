package og_spipes.service;

import com.google.common.collect.Sets;
import og_spipes.model.dto.SHACLValidationResultDTO;
import og_spipes.shacl.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.validation.SHACLException;
import org.topbraid.shacl.validation.ValidationReport;
import org.topbraid.shacl.validation.ValidationResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SHACLExecutorService {

    public Set<SHACLValidationResultDTO> testModel(Set<URL> ruleSet, String scriptPath) throws IOException, SHACLException{
        final Model dataModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, null);

//        OntDocumentManager.getInstance().setProcessImports(false);
        dataModel.read(new FileInputStream(scriptPath), "urn:dummy", FileUtils.langTurtle);

        final Set<SHACLValidationResultDTO> res = new HashSet<>();
        final Validator validator = new Validator();
        for(URL url : ruleSet){
            ValidationReport report = validator.validate(dataModel, Sets.newHashSet(url));
            for(ValidationResult result : report.results()){
                res.add(new SHACLValidationResultDTO(
                        result.getFocusNode().toString(),
                        result.getSeverity().getLocalName(),
                        result.getMessage(),
                        url.toString()
                ));
            }
        }

        dataModel.removeAll();

        return res;
    }

}
