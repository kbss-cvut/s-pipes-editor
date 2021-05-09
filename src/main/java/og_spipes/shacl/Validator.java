package og_spipes.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.progress.SimpleProgressMonitor;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.validation.ResourceValidationReport;
import org.topbraid.shacl.validation.ValidationReport;
import org.topbraid.shacl.validation.ValidationUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;


@SuppressWarnings("MissingJavadocType")
public class Validator {

//    private final Model shapesModel;
    private final Model mappingModel;

    /**
     * Validator constructor.
     */
    public Validator() {
        // inference rules
//        shapesModel = ModelFactory.createDefaultModel();
//        shapesModel.read(
//                new FileInputStream(new File("/home/chlupnoha/IdeaProjects/s-pipes-editor/src/test/resources/inference-rules.ttl")),
//            null,
//            FileUtils.langTurtle);

        mappingModel = ModelFactory.createDefaultModel();
        mappingModel.read(
                getClass().getClassLoader().getResourceAsStream("rules/model.ttl"),
                null,
                FileUtils.langTurtle);
    }

    private URL resource(final String name) {
        return getClass().getResource("/rules/" + name + ".ttl");
    }

    private Model getRulesModel(final Collection<URL> rules) throws IOException {
        final Model shapesModel = JenaUtil.createMemoryModel();
        for (URL r : rules) {
            shapesModel
                .read(r.openStream(), null, FileUtils.langTurtle);
        }
        return shapesModel;
    }

    /**
     * Validates the given model with vocabulary data (glossaries, models) against the given ruleset
     * and inference rules.
     *
     * @param dataModel model with data to validate
     * @param ruleSet   set of rules (see 'resources') used for validation
     * @return validation report
     */
    public ValidationReport validate(final Model dataModel, final Set<URL> ruleSet)
        throws IOException {
        final Model shapesModel = getRulesModel(ruleSet);
//        shapesModel.add(this.shapesModel);

        dataModel.add(mappingModel);

        final Model inferredModel = RuleUtil
            .executeRules(dataModel, shapesModel, null,
                new SimpleProgressMonitor("inference"));
        dataModel.add(inferredModel);

        final Resource report = ValidationUtil.validateModel(dataModel, shapesModel, true);

        return new ResourceValidationReport(report);
    }
}
