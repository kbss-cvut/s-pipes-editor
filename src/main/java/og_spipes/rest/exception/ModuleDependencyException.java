package og_spipes.rest.exception;

public class ModuleDependencyException extends RuntimeException {
    private final String URI;
    private final String script;
    private final String subscript;

    public ModuleDependencyException(String message, String URI, String script, String subscript) {
        super(message);
        this.URI = URI;
        this.script = script;
        this.subscript = subscript;
    }

    public String getURI() {
        return URI;
    }

    public String getScript() {
        return script;
    }

    public String getSubscript() {
        return subscript;
    }
}
