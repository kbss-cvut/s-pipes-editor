package og_spipes.rest.exception;

public class ModuleDependencyException extends RuntimeException {
    private final String script;
    private final String foundDependencySubscript;

    private final String dependeeModule;
    private final String dependantModule;

    public ModuleDependencyException(String message, String script, String foundDependencySubscript,
                                     String dependeeModule, String dependantModule) {
        super(message);
        this.script = script;
        this.foundDependencySubscript = foundDependencySubscript;
        this.dependeeModule = dependeeModule;
        this.dependantModule = dependantModule;
    }

    public String getScript() {
        return script;
    }

    public String getFoundDependencySubscript() {
        return foundDependencySubscript;
    }

    public String getDependeeModule() {
        return dependeeModule;
    }

    public String getDependantModule() {
        return dependantModule;
    }
}
