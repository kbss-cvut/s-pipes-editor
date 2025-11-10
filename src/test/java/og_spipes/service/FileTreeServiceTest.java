package og_spipes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import og_spipes.model.filetree.SubTree;
import og_spipes.testutil.AbstractSpringTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileTreeServiceTest extends AbstractSpringTest {

    private final FileTreeService fileTreeService = new FileTreeService();

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