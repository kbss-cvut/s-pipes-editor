package og_spipes.service;

import og_spipes.shacl.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.validation.SHACLException;
import org.topbraid.shacl.validation.ValidationReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Set;
import java.util.stream.Collectors;

public class SHACLExecutorService {

    public void testModel(Set<URL> ruleSet, String scriptPath) throws IOException, SHACLException{
        final Model dataModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, null);

        OntDocumentManager.getInstance().setProcessImports(false);
        dataModel.read(new FileInputStream(scriptPath), "urn:dummy", FileUtils.langTurtle);

        final Validator validator = new Validator();
        final ValidationReport r = validator.validate(dataModel, ruleSet);

        if(r.results().size() > 0){
            String errorMessage = StringUtils.join(r.results().stream().map(result -> MessageFormat
                    .format("[{0}] Node {1} failing for value {2} with message: {3} ",
                            result.getSeverity().getLocalName(), result.getFocusNode(), result.getValue(), result.getMessage())
            ).collect(Collectors.toList()), "\n");

            throw new SHACLException(errorMessage);
        }
    }

}
