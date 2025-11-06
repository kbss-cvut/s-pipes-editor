package og_spipes.service;

import com.google.common.collect.Sets;
import og_spipes.config.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.jena.sys.JenaSystem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ScriptOntologyHelperTest {

    private final String[] scriptPaths = new String[]{"/tmp/og_spipes"};

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @BeforeAll
    public static void initJena() {
        JenaSystem.init();
    }

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
    public void testModulesFileAssignment() {
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
                new File("/tmp/og_spipes/skosify/skosify.sms.ttl"),
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
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"), new File("/tmp/og_spipes/skosify/metadata.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/construct-example-data"), new File("/tmp/og_spipes/skosify/skosify.sms.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/skosify_Return"), new File("/tmp/og_spipes/skosify/skosify.sms.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"), new File("/tmp/og_spipes/skosify/relations.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels"), new File("/tmp/og_spipes/skosify/metadata.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/identify-concepts"), new File("/tmp/og_spipes/skosify/identification.ttl"));

        expectedRes.keySet().forEach(uri -> Assertions.assertEquals(expectedRes.get(uri), res.get(uri)));
    }

    @AfterEach
    public void after() {
        for(String scriptPath : scriptPaths){
            FileSystemUtils.deleteRecursively(new File(scriptPath));
        }
    }

}