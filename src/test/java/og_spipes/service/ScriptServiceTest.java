package og_spipes.service;

import og_spipes.config.Constants;
import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.service.exception.FileExistsException;
import og_spipes.service.exception.MissingOntologyException;
import og_spipes.service.exception.OntologyDuplicationException;
import og_spipes.testutil.AbstractSpringTest;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.sys.JenaSystem;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.jena.util.FileUtils.langTurtle;

public class ScriptServiceTest extends AbstractSpringTest {

    @Autowired
    private OntologyHelper ontologyHelper;

    @Autowired
    private ScriptService scriptService;

    @Value(Constants.SCRIPTPATH_SPEL)
    private String scriptPath;

    private Path scriptsRoot;

    @BeforeAll
    public static void initJena() {
        JenaSystem.init();
    }

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPath);
        scriptsRoot = scriptsHomeTmp.toPath();
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
    }

    @Test
    public void getModuleTypes() {
        Path file = scriptsRoot.resolve("hello-world").resolve("hello-world.sms.ttl");
        List<ModuleType> moduleTypes = scriptService.getModuleTypes(file.toString());
        Assertions.assertEquals(25, moduleTypes.size());
    }

    @Test
    public void getModule() {
        Path file = scriptsRoot.resolve("hello-world").resolve("hello-world.sms.ttl");
        List<Module> modules = scriptService.getModules(file.toString());
        Assertions.assertEquals(3, modules.size());
    }

    @Test
    public void moveModuleBasic() throws IOException {
        String moduleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname";
        Path from = scriptsRoot.resolve("hello-world/hello-world.sms.ttl");
        Path to   = scriptsRoot.resolve("hello-world/hello-world2.sms.ttl");

        scriptService.moveModule(from.toString(), to.toString(), moduleUri, false);

        Model fromModel = ontologyHelper.createOntModel(from.toFile());
        Model toModel   = ontologyHelper.createOntModel(to.toFile());

        String resUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname";
        List<Statement> fromStatements = fromModel.listStatements(fromModel.getResource(moduleUri), null, (RDFNode) null).toList();
        List<Statement> toStatements = toModel.listStatements(toModel.getResource(resUri), null, (RDFNode) null).toList();

        Assertions.assertEquals(0, fromStatements.size());
        Assertions.assertEquals(5, toStatements.size());
    }

    @Test
    public void moveModuleBasicWithRename() throws IOException {
        String moduleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname";
        Path from = scriptsRoot.resolve("hello-world/hello-world.sms.ttl");
        Path to   = scriptsRoot.resolve("hello-world/hello-world2.sms.ttl");

        scriptService.moveModule(from.toString(), to.toString(), moduleUri, true);

        Model fromModel = ontologyHelper.createOntModel(from.toFile());
        Model toModel = ontologyHelper.createOntModel(to.toFile());

        String resUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.2/bind-firstname";
        List<Statement> fromStatements = fromModel.listStatements(fromModel.getResource(moduleUri), null, (RDFNode) null).toList();
        List<Statement> toStatements = toModel.listStatements(toModel.getResource(resUri), null, (RDFNode) null).toList();

        Assertions.assertEquals(0, fromStatements.size());
        Assertions.assertEquals(7, toStatements.size());
    }

    @Test
    public void moveModuleAffectMoreFiles() throws IOException {
        String moduleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels";
        Path from = scriptsRoot.resolve("skosify/metadata.ttl");
        Path to   = scriptsRoot.resolve("skosify/identification.ttl");

        scriptService.moveModule(from.toString(), to.toString(), moduleUri, true);

        Model resModule = ModelFactory.createDefaultModel().read(scriptsRoot.resolve("skosify/skosify.sms.ttl").toString(), langTurtle);
        String resModuleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/construct-labels";
        List<Statement> toStatements = resModule.listStatements(resModule.getResource(resModuleUri), null, (RDFNode) null).toList();

        Assertions.assertEquals(1, toStatements.size());
    }

    @Test
    public void deleteModuleOnly() throws IOException {
        String moduleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels";

        scriptService.deleteModuleOnly(
                scriptsRoot.resolve("skosify/metadata.ttl").toString(),
                moduleUri
        );

        Model resModule = ModelFactory.createDefaultModel().read(scriptsRoot.resolve("skosify/metadata.ttl").toString(), langTurtle);
        List<Statement> toStatements = resModule.listStatements(resModule.getResource(moduleUri), null, (RDFNode) null).toList();

        Assertions.assertEquals(0, toStatements.size());
    }

    @Test
    public void createScript() throws OntologyDuplicationException, IOException, FileExistsException {
        scriptService.createScript(
                scriptsRoot.resolve("hello-world").toString(),
                "karel.ttl",
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/karel"),
                "say_Return",
                "say-hello-world",
                new ArrayList<>());



        File file = new File(scriptsRoot.resolve("hello-world/karel.ttl").toString());
        Assertions.assertTrue(file.exists());
    }

    @Test
    public void getScriptOntologies() {
        List<String> scriptImportedOntologies = scriptService
                .getScriptImportedOntologies(scriptsRoot.resolve("skosify/skosify.sms.ttl").toString());

        List<String> expectedRes = Arrays.asList(
            "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations",
            "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata"
        );

        Assertions.assertEquals(expectedRes, scriptImportedOntologies);
    }

    @Test
    public void addScriptOntology() throws IOException, MissingOntologyException {
        File f = scriptsRoot.resolve("skosify/skosify.sms.ttl").toFile();
        scriptService.addScriptOntology(
                f.getAbsolutePath(),
                "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/new-ontology"
        );

        Model defaultModel = ontologyHelper.createOntModel(f);
        List<Statement> statements = defaultModel
                .read(f.getAbsolutePath(), langTurtle)
                .listStatements(null, null, new ResourceImpl("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/new-ontology"))
                .toList();

        Assertions.assertEquals(1, statements.size());
    }

    @Test
    public void removeScriptOntology() throws IOException {
        File f = scriptsRoot.resolve("skosify/skosify.sms.ttl").toFile();
        scriptService.removeScriptOntology(
                f.getAbsolutePath(),
                "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata"
        );

        Model defaultModel = ontologyHelper.createOntModel(f);
        List<Statement> statements = defaultModel
                .read(f.getAbsolutePath(), langTurtle)
                .listStatements(null, null, new ResourceImpl("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata"))
                .toList();

        Assertions.assertEquals(0, statements.size());
    }

}