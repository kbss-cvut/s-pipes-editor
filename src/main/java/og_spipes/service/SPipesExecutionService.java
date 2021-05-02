package og_spipes.service;

import og_spipes.model.spipes.ExecutionDTO;
import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.persistence.dao.TransformationDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SPipesExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(SPipesExecutionService.class);

    private final String engineUrl;

    private final RestTemplate restTemplate;

    private final TransformationDAO transformationDAO;

    private final ScriptDAO scriptDAO;

    @Autowired
    public SPipesExecutionService(
            @Value("${engineurl}") String engineUrl,
            RestTemplate restTemplate,
            TransformationDAO transformationDAO,
            ScriptDAO scriptDAO
    ) {
        this.engineUrl = engineUrl;
        this.restTemplate = restTemplate;
        this.transformationDAO = transformationDAO;
        this.scriptDAO = scriptDAO;
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

    public List<ExecutionDTO> getAllExecution() {
        return transformationDAO.getAllExecutionTransformation().stream().map(x -> {
            Map<String, Set<Object>> properties = x.getProperties();
            String pipelineURI = properties.get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-name").stream().findFirst().orElse("").toString();
            String name = pipelineURI.substring(pipelineURI.lastIndexOf("/")+1);
            String duration = properties.get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-duration").stream().findFirst().orElse("").toString();
            Date startDate = (Date) properties.get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-start-date").stream().findFirst().orElse(new Date());
            Date finishDate = (Date) properties.get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-finish-date").stream().findFirst().orElse(new Date());
            try {
                return new ExecutionDTO(
                        pipelineURI,
                        name,
                        scriptDAO.findScriptByOntologyName(pipelineURI).getAbsolutePath(),
                        Long.parseLong(duration),
                        startDate,
                        finishDate,
                        x.getId()
                );
            } catch (IOException e) {
                LOG.warn(e.getMessage());
                return null;
            }
        }).collect(Collectors.toList());
    }

}