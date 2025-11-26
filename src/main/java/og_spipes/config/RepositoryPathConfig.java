package og_spipes.config;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class RepositoryPathConfig {

    Path rootDir;

    @Bean(name = "rdf4jRepositoryUrl")
    @Profile("test")
    public String testRepositoryUrl() throws IOException {
        rootDir = Files.createTempDirectory("og_spipes_sesame");

        Path repoDir = rootDir.resolve("repo")
                .resolve("repositories")
                .resolve("s-pipes-hello-world");
        Files.createDirectories(repoDir);
        return repoDir.toUri().toString();
    }

    @Bean(name = "rdf4jRepositoryUrl")
    @Profile("default")
    public String prodRepositoryUrl(@Value("${rdf4j.repositoryUrl}") String repositoryUrl) {
        return repositoryUrl;
    }

    @Bean(name = "rdf4jRepositoryName")
    @Profile("test")
    public String testRepositoryName() {
        return "s-pipes-hello-world";
    }

    @Bean(name = "rdf4jRepositoryName")
    @Profile("default")
    public String prodRepositoryName(@Value("${rdf4j.repositorName}") String repositoryName) {
        return repositoryName;
    }

    @PreDestroy
    @Profile("test")
    private void clean() {
        FileSystemUtils.deleteRecursively(rootDir.toFile());
    }

}
