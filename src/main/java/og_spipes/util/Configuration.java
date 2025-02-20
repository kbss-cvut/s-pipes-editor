package og_spipes.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "")
public class Configuration {

    String scriptPaths;
    String engineurl;
    String scriptRules;

    Rdf4j rdf4j = new Rdf4j();

    public String getScriptPaths() {
        return scriptPaths;
    }

    public void setScriptPaths(String scriptPaths) {
        this.scriptPaths = scriptPaths;
    }

    public String getEngineurl() {
        return engineurl;
    }

    public void setEngineurl(String engineurl) {
        this.engineurl = engineurl;
    }

    public String getScriptRules() {
        return scriptRules;
    }

    public void setScriptRules(String scriptRules) {
        this.scriptRules = scriptRules;
    }

    public Rdf4j getRdf4j() {
        return rdf4j;
    }

    public void setRdf4j(Rdf4j rdf4j) {
        this.rdf4j = rdf4j;
    }

    public static class Rdf4j {
        String repositoryUrl;
        String workbenchUrlRepository;
        String repositoryName;
        String pConfigURL;
        String driver;

        public String getRepositoryUrl() {
            return repositoryUrl;
        }

        public void setRepositoryUrl(String repositoryUrl) {
            this.repositoryUrl = repositoryUrl;
        }

        public String getWorkbenchUrlRepository() {
            return workbenchUrlRepository;
        }

        public void setWorkbenchUrlRepository(String workbenchUrlRepository) {
            this.workbenchUrlRepository = workbenchUrlRepository;
        }

        public String getRepositoryName() {
            return repositoryName;
        }

        public void setRepositoryName(String repositoryName) {
            this.repositoryName = repositoryName;
        }

        public String getpConfigURL() {
            return pConfigURL;
        }

        public void setpConfigURL(String pConfigURL) {
            this.pConfigURL = pConfigURL;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }
    }
}
