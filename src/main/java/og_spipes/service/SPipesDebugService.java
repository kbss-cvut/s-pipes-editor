package og_spipes.service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import og_spipes.service.exception.SPipesEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class SPipesDebugService {
    private static final Logger LOG = LoggerFactory.getLogger(SPipesDebugService.class);
    private final String debugUrl;

    public SPipesDebugService(@Value("${debugUrl}") String debugUrl) {
        this.debugUrl = debugUrl;
    }

    public String getAllExecutions() throws SPipesEngineException {
        return makeGetRequest("/executions");
    }

    public String getExecution(String executionId) throws SPipesEngineException {
        validateId(executionId);
        return makeGetRequest("/executions/" + executionId);
    }

    public String getExecutionModules(String executionId) throws SPipesEngineException {
        validateId(executionId);
        return makeGetRequest("/executions/" + executionId + "/modules");
    }

    public String compareExecutions(String executionId, String compareToId) throws SPipesEngineException {
        validateId(executionId);
        validateId(compareToId);
        return makeGetRequest("/executions/" + executionId + "/compare/" + compareToId);
    }

    public String findTripleOrigin(String executionId, String graphPattern) throws SPipesEngineException {
        validateId(executionId);
        return makeGetRequest("/triple-origin/" + executionId, "graphPattern", graphPattern);
    }

    public String findTripleElimination(String executionId, String graphPattern) throws SPipesEngineException {
        validateId(executionId);
        return makeGetRequest("/triple-elimination/" + executionId, "graphPattern", graphPattern);
    }

    public String findVariableOrigin(String executionId, String variable) throws SPipesEngineException {
        validateId(executionId);
        return makeGetRequest("/variable-origin/" + executionId, "variable", variable);
    }

    private String makeGetRequest(String path) throws SPipesEngineException {
        return makeGetRequest(path, null, null);
    }

    private String makeGetRequest(String path, String paramName, String paramValue) throws SPipesEngineException {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(debugUrl + path);
            if (paramName != null && paramValue != null) {
                builder.queryParam(paramName, paramValue);
            }
            LOG.info("SPipes debug query: " + builder.build());
            HttpResponse<String> request = Unirest.get(builder.build().toString()).asString();
            if (request.getStatus() != 200) {
                LOG.error("Debug service error: {}", request.getBody());
            }
            return request.getBody();
        } catch (Exception e) {
            LOG.error("HTTP request failed: {}", e.getMessage(), e);
            throw new SPipesEngineException("Debug service unavailable", e);
        }
    }

    private void validateId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Execution ID cannot be null or empty");
        }
    }


}
