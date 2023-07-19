package og_spipes.config;

import og_spipes.service.SHACLExecutorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        // TODO - remove bean dependency
        classes = {SHACLExecutorService.class}, // reduce dependency of test to other beans
        properties = {"scriptPaths=/tmp/og_spipes;/tmp/og_spipes1"} // tested balue
)
public class CustomParsingScriptPathParameterTest {
    @Value(Constants.SCRIPTPATH_SPEL)
    private String[] scriptPaths;


    @Test
    void testImport(){
        Assertions.assertEquals(2, scriptPaths.length);
    }
}
