package og_spipes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import og_spipes.model.filetree.SubTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FileTreeServiceTest {

    private final FileTreeService fileTreeService = new FileTreeService();

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @Test
    public void getTtlFileTree() throws JsonProcessingException {
        SubTree ttlFileTree = fileTreeService.getTtlFileTree(new File("src/test/resources/ttl_files"));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(ttlFileTree);

        long foldersFilesCount = Arrays.stream(json.split("\"name\"")).count();
        long foldersCount = Arrays.stream(json.split("\"children\"")).count();
        assertEquals(5, foldersCount);
        assertEquals(10, foldersFilesCount);
    }
}