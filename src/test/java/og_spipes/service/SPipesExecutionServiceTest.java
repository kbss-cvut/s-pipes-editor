package og_spipes.service;

import com.google.common.collect.ImmutableMap;
import og_spipes.model.spipes.FunctionDTO;
import og_spipes.persistence.dao.ScriptDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class SPipesExecutionServiceTest {

    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    private final SPipesExecutionService service = new SPipesExecutionService("http://localhost:1111", restTemplate);

    @Test
    public void serviceExecution(){
        when(restTemplate.getForEntity(
                "http://localhost:1111/service?id=execute-greeding",
                String.class,
                new HashMap<String, String>() {{
                    put("firstName","karel");
                    put("repositoryName","http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1");
                }})
        ).thenReturn(new ResponseEntity<>("body", HttpStatus.ACCEPTED));

        ResponseEntity<String> entity = service.serviceExecution(
                "execute-greeding",
                "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1",
                new HashMap<String, String>() {{
                    put("firstName","karel");
                }}
        );

        assertEquals(new ResponseEntity<>("body", HttpStatus.ACCEPTED), entity);
    }

}