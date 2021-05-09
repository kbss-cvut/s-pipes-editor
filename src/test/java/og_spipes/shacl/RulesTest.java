package og_spipes.shacl;

import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import cz.cvut.kbss.ontodriver.jena.config.JenaOntoDriverProperties;
import og_spipes.service.SHACLExecutorService;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.SHACLPreferences;
import org.topbraid.shacl.validation.SHACLException;
import org.topbraid.shacl.validation.ValidationReport;
import org.topbraid.shacl.vocabulary.SH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


public class RulesTest {

    private final SHACLExecutorService executorService = new SHACLExecutorService();

    @Test
    public void testAllShaclRule() throws IOException {
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

    private void testModel(Set<URL> ruleSet, String data, Outcome outcome) throws IOException {
        try {
            executorService.testModel(ruleSet, data);
            Assertions.assertEquals(outcome, Outcome.Pass);
        } catch (SHACLException e) {
            Assertions.assertTrue(outcome != Outcome.Pass);
            Assertions.assertNotNull(e.getMessage());
        }
    }

    enum Outcome {
        Info(SH.Info), Warning(SH.Warning), Violation(SH.Violation), Pass(null);
        Resource url;

        Outcome(Resource url) {
            this.url = url;
        }
    }
}
