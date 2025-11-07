package og_spipes.service;

import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.view.View;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ViewServiceTest {

    @Mock
    private ScriptService scriptService;

    @InjectMocks
    private ViewService viewService;

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("rdf4j.repositoryUrl", () -> tempDir.resolve("repositories").toUri().toString());
    }

    @Test
    public void newViewFromSpipes() throws URISyntaxException {
        //TODO boost up test with not empty next
        ArrayList<Module> mockModule = new ArrayList<>();
        mockModule.add(new Module(
                new URI("www.example.com"),
                "id",
                "label",
                new HashSet<>(),
                new ModuleType(),
                new HashSet<>(),
                null,
                "21",
                "31",
                "scriptPath",
                "source"

        ));
        when(scriptService.getModules(any())).thenReturn(mockModule);

        View view = viewService.newViewFromSpipes("dummy.ttl", null);

        Assertions.assertEquals("dummy.ttl", view.getLabel());
        Assertions.assertEquals(1, view.getNodes().size());
        Assertions.assertNotNull(view.getUri(), "URI is generated");
        Assertions.assertNotNull(view.getId(), "ID is generated");
    }

}