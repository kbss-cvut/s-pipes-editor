package og_spipes.service;

import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.view.View;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
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

        Assertions.assertEquals("dummy.ttl", view.getLabel());
        Assertions.assertEquals(1, view.getNodes().size());
        Assertions.assertNotNull(view.getUri(), "URI is generated");
        Assertions.assertNotNull(view.getId(), "ID is generated");
    }

}