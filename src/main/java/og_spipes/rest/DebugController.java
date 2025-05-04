package og_spipes.rest;

import og_spipes.service.SPipesDebugService;
import og_spipes.service.exception.SPipesEngineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/log")
public class DebugController {

    private final SPipesDebugService debugService;

    @Autowired
    public DebugController(SPipesDebugService debugService) {
        this.debugService = debugService;
    }


    @GetMapping("/executions")
    public String getAllExecutions() throws SPipesEngineException {
        return debugService.getAllExecutions();
    }

    @GetMapping("/executions/{executionId}")
    public String getExecutionByRequestBody(@PathVariable("executionId") String executionId) throws SPipesEngineException {
        return debugService.getExecution(executionId);
    }

    @GetMapping("/executions/{executionId}/modules")
    public String getExecutionModules(@PathVariable("executionId") String executionId) throws SPipesEngineException {
//        System.out.println(requestBody.toString());
//        String transformationId = (String) requestBody.get(s_p_has_transformation_id);
//        String sortBy = (String) requestBody.get(sortBy);
//        String orderType = (String) requestBody.get(orderType);

        return debugService.getExecutionModules(executionId);
//        return debugService.getExecutionModules(transformationId, sortBy, orderType);
    }

    @GetMapping("/executions/{executionId}/compare/{compareToId}")
    public String compareExecutions(@PathVariable("executionId") String executionId, @PathVariable("compareToId") String compareToId) throws SPipesEngineException {
        return debugService.compareExecutions(executionId, compareToId);
    }

    @GetMapping("/triple-origin/{executionId}")
    public String findTripleOrigin(@PathVariable("executionId") String executionId, @RequestParam(name = "graphPattern") String graphPattern) throws SPipesEngineException {
        String encodedGraphPattern = URLEncoder.encode(graphPattern, StandardCharsets.UTF_8);
        return debugService.findTripleOrigin(executionId, encodedGraphPattern);
    }

    @GetMapping("/triple-elimination/{executionId}")
    public String findTripleElimination(@PathVariable("executionId") String executionId, @RequestParam(name = "graphPattern") String graphPattern) throws SPipesEngineException {
        String encodedGraphPattern = URLEncoder.encode(graphPattern, StandardCharsets.UTF_8);
        return debugService.findTripleElimination(executionId, encodedGraphPattern);
    }

    @GetMapping("/variable-origin/{executionId}")
    public String findVariableOrigin(@PathVariable("executionId") String executionId, @RequestParam(name = "variable") String variable) throws SPipesEngineException {
        String encodedVariable = URLEncoder.encode(variable, StandardCharsets.UTF_8);
        return debugService.findVariableOrigin(executionId, encodedVariable);
    }


}
