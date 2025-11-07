package og_spipes.service.util;

import org.apache.commons.compress.utils.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ScriptImportGroupTest {

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @Test
    public void findFilesTest() {
        File testScript = new File("src/test/resources/scripts_test/sample/skosify/skosify.sms.ttl");
        ScriptImportGroup importGroup = new ScriptImportGroup("src/test/resources/scripts_test/sample".split(","), testScript);

        Set<String> res = importGroup.getUsedFiles().stream().map(File::getName).collect(Collectors.toSet());

        HashSet<String> expectedRes = Sets.newHashSet("skosify.sms.ttl", "relations.ttl", "metadata.ttl", "identification.ttl");
        Assertions.assertEquals(
                expectedRes,
                res
        );
    }


}