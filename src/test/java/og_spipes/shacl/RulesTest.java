package og_spipes.shacl;

import og_spipes.config.Constants;
import og_spipes.model.dto.SHACLValidationResultDTO;
import og_spipes.service.SHACLExecutorService;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;
import org.topbraid.shacl.vocabulary.SH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class RulesTest {

    @Value(Constants.SCRIPTPATH_SPEL)
    private String[] scriptPaths;

    @BeforeEach
    public void init() throws Exception {
        for(String scriptPath : scriptPaths){
            File scriptsHomeTmp = new File(scriptPath);
            if(scriptsHomeTmp.exists()){
                FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            }
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
    }

    @Test
    public void testValidationError() throws IOException, URISyntaxException {
        SHACLExecutorService executorService= new SHACLExecutorService(scriptPaths);
        Set<SHACLValidationResultDTO> resultDTOS = executorService.testModel(
                Collections.singleton(new File("src/main/resources/rules/SHACL/module-requires-rdfs_label.ttl").toURL()),
                new File("src/test/resources/SHACL/rule-test-cases/data-without-label.ttl").getAbsolutePath()
        );

        Assertions.assertEquals(1, resultDTOS.size());
    }

    @Test
    public void testAllShaclRule() throws IOException, URISyntaxException {
        File file = new File("src/test/resources/SHACL/test-cases.csv");
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> collect = Arrays.stream(line.split(",")).map(x -> x.trim()).collect(Collectors.toList());
                records.add(collect);
            }
        }
        List<List<String>> recordsSkip = records.stream().skip(1).collect(Collectors.toList());
        for(List<String> r : recordsSkip){
            testModel(
                    Collections.singleton(new File("src/main/resources/rules/SHACL/" + r.get(0)).toURL()),
                    new File("src/test/resources/SHACL/" + r.get(1)).getAbsolutePath(),
                    Outcome.valueOf(r.get(2))
            );
        }
    }

    private void testModel(Set<URL> ruleSet, String data, Outcome outcome) throws IOException, URISyntaxException {
        SHACLExecutorService executorService= new SHACLExecutorService(scriptPaths);
        Set<SHACLValidationResultDTO> resultDTOS = executorService.testModel(ruleSet, data);
        if(resultDTOS.size() > 0){
            Assertions.assertNotSame(outcome, Outcome.Pass);
        }else{
            Assertions.assertEquals(outcome, Outcome.Pass);
        }
    }

    enum Outcome {
        Info(SH.Info), Warning(SH.Warning), Violation(SH.Violation), Pass(null);
        Resource url;

        Outcome(Resource url) {
            this.url = url;
        }
    }

    @AfterEach
    public void after() {
        for(String scriptPath : scriptPaths){
            FileSystemUtils.deleteRecursively(new File(scriptPath));
        }
    }

}
