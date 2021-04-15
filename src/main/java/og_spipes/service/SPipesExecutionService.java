package og_spipes.service;

import og_spipes.model.spipes.FunctionDTO;
import og_spipes.persistence.dao.ScriptDao;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class SPipesExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(SPipesExecutionService.class);

    private final String engineUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public SPipesExecutionService(@Value("${engineurl}") String engineUrl, RestTemplate restTemplate) {
        this.engineUrl = engineUrl;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> serviceExecution (
            String functionId,
            String repositoryName,
            Map<String, String> params
    ) {
        String serviceUrl = engineUrl + "/service";

        params.put("repositoryName", repositoryName);

        return restTemplate.getForEntity(serviceUrl + "?id=" + functionId, String.class, params);
    }

}
