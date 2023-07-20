package og_spipes.service;

import og_spipes.config.Constants;
import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.service.exception.FileExistsException;
import og_spipes.service.exception.MissingOntologyException;
import og_spipes.service.exception.OntologyDuplicationException;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.apache.jena.util.FileUtils.langTurtle;

@SpringBootTest
public class ScriptServiceTest {

    @Autowired
    private OntologyHelper ontologyHelper;

    @Autowired
    private ScriptService scriptService;

    @Value(Constants.SCRIPTPATH_SPEL)
    private String[] scriptPaths;

    @BeforeEach
    public void init() throws Exception {
        for(String scriptPath : scriptPaths){
            File scriptsHomeTmp = new File(scriptPath);
            if(scriptsHomeTmp.exists()){
                FileSystemUtils.deleteRecursively(scriptsHomeTmp);
                Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
            }
            FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
        }
    }

    @Test
    public void getModuleTypes() {
        List<ModuleType> moduleTypes = scriptService.getModuleTypes("/tmp/og_spipes/hello-world/hello-world.sms.ttl");

        Assertions.assertEquals(85, moduleTypes.size());
    }

    @Test
    public void getModule() {
        List<Module> modules = scriptService.getModules("/tmp/og_spipes/hello-world/hello-world.sms.ttl");

        Assertions.assertEquals(3, modules.size());
    }

    @Test
    public void moveModuleBasic() throws FileNotFoundException {
        String moduleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname";

        scriptService.moveModule(
                "/tmp/og_spipes/hello-world/hello-world.sms.ttl",
                "/tmp/og_spipes/hello-world/hello-world2.sms.ttl",
                moduleUri,
                false
        );

        Model fromModel = ontologyHelper.createOntModel(new File("/tmp/og_spipes/hello-world/hello-world.sms.ttl"));
        Model toModel = ontologyHelper.createOntModel(new File("/tmp/og_spipes/hello-world/hello-world2.sms.ttl"));

        String resUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname";
        List<Statement> fromStatements = fromModel.listStatements(fromModel.getResource(moduleUri), null, (RDFNode) null).toList();
        List<Statement> toStatements = toModel.listStatements(toModel.getResource(resUri), null, (RDFNode) null).toList();

        Assertions.assertEquals(fromStatements.size(), 0);
        Assertions.assertEquals(toStatements.size(), 5);
    }

    @Test
    public void moveModuleBasicWithRename() throws FileNotFoundException {
        String moduleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/bind-firstname";

        scriptService.moveModule(
                "/tmp/og_spipes/hello-world/hello-world.sms.ttl",
                "/tmp/og_spipes/hello-world/hello-world2.sms.ttl",
                moduleUri,
                true
        );

        Model fromModel = ontologyHelper.createOntModel(new File("/tmp/og_spipes/hello-world/hello-world.sms.ttl"));
        Model toModel = ontologyHelper.createOntModel(new File("/tmp/og_spipes/hello-world/hello-world2.sms.ttl"));

        String resUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.2/bind-firstname";
        List<Statement> fromStatements = fromModel.listStatements(fromModel.getResource(moduleUri), null, (RDFNode) null).toList();
        List<Statement> toStatements = toModel.listStatements(toModel.getResource(resUri), null, (RDFNode) null).toList();

        Assertions.assertEquals(0, fromStatements.size());
        Assertions.assertEquals(7, toStatements.size());
    }

    @Test
    public void moveModuleAffectMoreFiles() throws FileNotFoundException {
        String moduleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels";

        scriptService.moveModule(
                "/tmp/og_spipes/skosify/metadata.ttl",
                "/tmp/og_spipes/skosify/identification.ttl",
                moduleUri,
                true
        );

        Model resModule = ModelFactory.createDefaultModel().read("/tmp/og_spipes/skosify/skosify.sms.ttl", langTurtle);
        String resModuleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/construct-labels";
        List<Statement> toStatements = resModule.listStatements(resModule.getResource(resModuleUri), null, (RDFNode) null).toList();

        Assertions.assertEquals(1, toStatements.size());
    }

    @Test
    public void deleteModuleOnly() throws FileNotFoundException {
        String moduleUri = "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels";

        scriptService.deleteModuleOnly(
                "/tmp/og_spipes/skosify/metadata.ttl",
                moduleUri
        );

        Model resModule = ModelFactory.createDefaultModel().read("/tmp/og_spipes/skosify/metadata.ttl", langTurtle);
        List<Statement> toStatements = resModule.listStatements(resModule.getResource(moduleUri), null, (RDFNode) null).toList();

        Assertions.assertEquals(0, toStatements.size());
    }

    @Test
    public void createScript() throws OntologyDuplicationException, IOException, FileExistsException {
        scriptService.createScript(
                "/tmp/og_spipes/hello-world",
                "karel.ttl",
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/karel")
        );

        File file = new File("/tmp/og_spipes/hello-world/karel.ttl");
        Assertions.assertTrue(file.exists());
    }

    @Test
    public void getScriptOntologies() {
        List<String> scriptImportedOntologies = scriptService
                .getScriptImportedOntologies("/tmp/og_spipes/skosify/skosify.sms.ttl");

        List<String> expectedRes = Arrays.asList(
                "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata",
                "http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations"
        );

        Assertions.assertEquals(expectedRes, scriptImportedOntologies);
    }

    @Test
    public void addScriptOntology() throws FileNotFoundException, MissingOntologyException {
        File f = new File("/tmp/og_spipes/skosify/skosify.sms.ttl");
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
    public void removeScriptOntology() throws FileNotFoundException {
        File f = new File("/tmp/og_spipes/skosify/skosify.sms.ttl");
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

//    @AfterEach
//    public void after() {
//        for(String scriptPath : scriptPaths){
//            FileSystemUtils.
//            Recursively(new File(scriptPath));
//        }
//    }

}