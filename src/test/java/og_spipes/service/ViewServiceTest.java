package og_spipes.service;

import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ViewServiceTest {

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
                new HashSet<>()
        ));
        when(scriptService.getModules(any())).thenReturn(mockModule);

        View view = viewService.newViewFromSpipes("dummy.ttl");

        assertEquals("dummy.ttl", view.getLabel());
        assertEquals(1, view.getNodes().size());
        assertNotNull("URI is generated", view.getUri());
        assertNotNull("ID is generated", view.getId());
    }

}