package og_spipes.service;

import com.google.common.collect.ImmutableMap;
import og_spipes.model.spipes.FunctionDTO;
import og_spipes.model.spipes.ModuleType;
import og_spipes.persistence.dao.ScriptDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FunctionServiceTest {

    @Mock
    private ScriptDao scriptDao;

    @Mock
    private RestTemplate restTemplate = new RestTemplate();

    @InjectMocks
    private FunctionService functionService;

    @Test
    public void moduleFunctions() throws URISyntaxException {
        ArrayList<FunctionDTO> mockedFunctions = new ArrayList<>();
        mockedFunctions.add(new FunctionDTO());
        when(scriptDao.moduleFunctions(any())).thenReturn(mockedFunctions);
        URL resource = FunctionServiceTest.class.getClassLoader().getResource("scripts_test/sample/sample-script.ttl");

        List<FunctionDTO> functionDTOS = functionService.moduleFunctions(
                Paths.get(resource.toURI()).toFile().getAbsolutePath()
        );

        assertEquals(1, functionDTOS.size());
    }

}