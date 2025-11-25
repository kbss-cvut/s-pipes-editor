package og_spipes.config;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class ScriptPathConfig {

    Path scriptDir;

    @Bean
    @Profile("test")
    @Qualifier("scriptPath")
    public String TestScriptPath() throws IOException {
        scriptDir = Files.createTempDirectory("og_spipes_scripts");
        Files.createDirectories(scriptDir);

        return scriptDir.toUri().toString();
    }

    @Bean
    @Profile("default")
    @Qualifier("scriptPath")
    public String prodScriptPath(@Value("${scriptPaths}") String scriptPath) {
        return scriptPath;
    }

    @PreDestroy
    @Profile("test")
    private void clean() {
        FileSystemUtils.deleteRecursively(scriptDir.toFile());
    }
}

