package og_spipes.shacl;

import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.topbraid.shacl.validation.ResourceValidationResult;
import org.topbraid.shacl.validation.ValidationResult;

import java.nio.file.Path;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ValidationResultSeverityComparatorTest {

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    private ValidationResult mockWithSeverity(final String severityUri) {
        final ValidationResult result1 = mock(ResourceValidationResult.class);
        when(result1.getSeverity()).thenReturn(ResourceFactory.createResource(
            severityUri));
        return result1;
    }

    private void testEquals(final String severityUri1, final String severityUri2) {
        ValidationResult result1 = mockWithSeverity(
            severityUri1);
        ValidationResult result2 = mockWithSeverity(
            severityUri2);
        Assertions.assertEquals( 0, new ValidationResultSeverityComparator().compare(result1,result2) );
    }

    private void testGreater(final String severityUri1, final String severityUri2) {
        ValidationResult result1 = mockWithSeverity(
            severityUri1);
        ValidationResult result2 = mockWithSeverity(
            severityUri2);
        Assertions.assertTrue( new ValidationResultSeverityComparator().compare(result1,result2) > 0);
    }

    @Test
    public void compareComparesCorrectlySameValue() {
        testEquals(
            ShaclSeverity.VIOLATION.getUri(),
            ShaclSeverity.VIOLATION.getUri());

        testEquals(
            ShaclSeverity.WARNING.getUri(),
            ShaclSeverity.WARNING.getUri());

        testEquals(
            ShaclSeverity.INFO.getUri(),
            ShaclSeverity.INFO.getUri());
    }

    @Test
    public void compareComparesCorrectlyLessThanValue() {
        testGreater(
            ShaclSeverity.WARNING.getUri(),
            ShaclSeverity.VIOLATION.getUri()
        );

        testGreater(
            ShaclSeverity.INFO.getUri(),
            ShaclSeverity.WARNING.getUri()
        );

        testGreater(
            ShaclSeverity.INFO.getUri(),
            ShaclSeverity.VIOLATION.getUri()
        );
    }

    @Test
    public void compareComparesCorrectlyGreaterThanValue() {
        testGreater(
            ShaclSeverity.WARNING.getUri(),
            ShaclSeverity.VIOLATION.getUri()
        );

        testGreater(
            ShaclSeverity.INFO.getUri(),
            ShaclSeverity.WARNING.getUri()
        );

        testGreater(
            ShaclSeverity.INFO.getUri(),
            ShaclSeverity.VIOLATION.getUri()
        );
    }
}
