package og_spipes.service;

import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.view.View;
import og_spipes.testutil.AbstractSpringTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ViewServiceTest extends AbstractSpringTest {

    @Mock
    private ScriptService scriptService;

    @InjectMocks
    private ViewService viewService;

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