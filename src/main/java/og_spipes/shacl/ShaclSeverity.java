package og_spipes.shacl;

import org.topbraid.shacl.vocabulary.SH;

/**
 * SHACL severity.
 */
public enum ShaclSeverity {
    VIOLATION(SH.Violation.getURI()),
    WARNING(SH.Warning.getURI()),
    INFO(SH.Info.getURI());

    private final String uri;

    ShaclSeverity(final String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
