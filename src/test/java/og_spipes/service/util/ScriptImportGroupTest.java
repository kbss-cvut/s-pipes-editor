package og_spipes.service.util;

import org.apache.commons.compress.utils.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
class ScriptImportGroupTest {

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