package og_spipes.shacl;

import org.topbraid.shacl.validation.ValidationResult;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class ValidationResultSeverityComparator implements Comparator<ValidationResult> {

    private static ShaclSeverity of(final ValidationResult result) {
        final Optional<ShaclSeverity> severity
            = Arrays.stream(ShaclSeverity.values()).filter(s ->
            s.getUri().equals(result.getSeverity().getURI())
        ).findFirst();
        return severity.orElse(null);
    }

    /**
     * Compares results based on the severity.
     *
     * @param res1 first validation result
     * @param res2 second validation result
     * @return negative, 0, positive as per comparison contract
     */
    public int compare(ValidationResult res1, ValidationResult res2) {
        return ValidationResultSeverityComparator.of(res1).compareTo(
            ValidationResultSeverityComparator.of(res2)
        );
    }

}
