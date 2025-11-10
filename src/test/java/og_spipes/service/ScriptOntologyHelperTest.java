package og_spipes.service;

import com.google.common.collect.Sets;
import og_spipes.config.Constants;
import og_spipes.testutil.AbstractSpringTest;
import org.apache.commons.io.FileUtils;
import org.apache.jena.sys.JenaSystem;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ScriptOntologyHelperTest extends AbstractSpringTest {

    @Value(Constants.SCRIPTPATH_SPEL)
    private String[] scriptPaths;

    @BeforeAll
    public static void initJena() {
        JenaSystem.init();
    }

    @BeforeEach
    public void init() throws Exception {
        for(String scriptPath : scriptPaths){
            File scriptsHomeTmp = new File(scriptPath);
            FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
        }
    }

    @Test
    public void testModulesFileAssignment() {
        Path scriptsDir = tempDir.resolve("scripts").resolve("skosify");
        ScriptOntologyHelper scriptOntologyHelper = new ScriptOntologyHelper(scriptPaths);
        HashSet<URI> uris = Sets.newHashSet(
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/identify-concepts"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/skosify_Return"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/construct-example-data"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/bind-prefered-label-property")
        );

        Map<URI, Set<String>> res = scriptOntologyHelper.resolveFileGroups(
                scriptsDir.resolve("skosify.sms.ttl").toFile(),
                uris
        );

        Assertions.assertEquals(res.get(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/bind-prefered-label-property")), Sets.newHashSet("metadata.ttl"));
        Assertions.assertEquals(res.get(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/construct-example-data")), Sets.newHashSet("skosify.sms.ttl"));
        Assertions.assertEquals(res.get(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader")), Sets.newHashSet("skosify.sms.ttl", "relations.ttl"));
        Assertions.assertEquals(res.get(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/skosify_Return")), Sets.newHashSet("skosify.sms.ttl"));
        Assertions.assertEquals(res.get(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels")), Sets.newHashSet("skosify.sms.ttl", "metadata.ttl"));
        Assertions.assertEquals(res.get(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/identify-concepts")), Sets.newHashSet("relations.ttl", "metadata.ttl", "identification.ttl"));
    }

    @Test
    public void testGetModule2FileMappingBasedOnPrefix() {
        Path scriptsDir = tempDir.resolve("scripts").resolve("skosify");
        ScriptOntologyHelper scriptOntologyHelper = new ScriptOntologyHelper(scriptPaths);
        HashSet<URI> uris = Sets.newHashSet(
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/identify-concepts"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/skosify_Return"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/construct-example-data"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/bind-prefered-label-property")
        );

        Map<URI, File> res = scriptOntologyHelper.getModule2FileMappingBasedOnPrefix(uris);

        Map<URI, File> expectedRes = new HashMap<>();
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"), scriptsDir.resolve("metadata.ttl").toFile());
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/construct-example-data"), scriptsDir.resolve("skosify.sms.ttl").toFile());
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/skosify_Return"), scriptsDir.resolve("skosify.sms.ttl").toFile());
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"), scriptsDir.resolve("relations.ttl").toFile());
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels"), scriptsDir.resolve("metadata.ttl").toFile());
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/identify-concepts"), scriptsDir.resolve("identification.ttl").toFile());

        expectedRes.keySet().forEach(uri -> Assertions.assertEquals(expectedRes.get(uri), res.get(uri)));
    }

}