package og_spipes.service;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class ScriptGroupsHelperTest {

    @Value("${scriptPaths}")
    private String scriptPaths;

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPaths);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
    }

    @Test
    public void testModulesFileAssignment() {
        ScriptGroupsHelper scriptGroupsHelper = new ScriptGroupsHelper(scriptPaths);
        HashSet<URI> uris = Sets.newHashSet(
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/identify-concepts"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/skosify_Return"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/construct-example-data"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/bind-prefered-label-property")
        );

        Map<URI, Set<String>> res = scriptGroupsHelper.resolveFileGroups(
                new File(scriptPaths + "/skosify/skosify.sms.ttl"),
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
    public void testModuleFile() {
        ScriptGroupsHelper scriptGroupsHelper = new ScriptGroupsHelper(scriptPaths);
        HashSet<URI> uris = Sets.newHashSet(
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/identify-concepts"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/skosify_Return"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/construct-example-data"),
                URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/bind-prefered-label-property")
        );

        Map<URI, File> res = scriptGroupsHelper.moduleFile(uris);

        Map<URI, File> expectedRes = new HashMap<>();
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"), new File(scriptPaths + "/skosify/metadata.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/construct-example-data"), new File(scriptPaths + "/skosify/skosify.sms.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/skosify_Return"), new File(scriptPaths + "/skosify/skosify.sms.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/relations/construct-broader"), new File(scriptPaths + "/skosify/relations.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/metadata/construct-labels"), new File(scriptPaths + "/skosify/metadata.ttl"));
        expectedRes.put(URI.create("http://onto.fel.cvut.cz/ontologies/s-pipes/skosify-example-0.1/identification/identify-concepts"), new File(scriptPaths + "/skosify/identification.ttl"));

        expectedRes.keySet().forEach(uri -> Assertions.assertEquals(expectedRes.get(uri), res.get(uri)));
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(scriptPaths));
    }

}