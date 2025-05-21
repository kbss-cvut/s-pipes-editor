package og_spipes.rest;

import og_spipes.model.dto.ScriptDTO;
import og_spipes.model.spipes.ExecutionDTO;
import og_spipes.service.FunctionService;
import og_spipes.service.ModuleExecutionInfo;
import og_spipes.service.SPipesExecutionService;
import og_spipes.service.ViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/execution")
public class ExecutionController {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionController.class);

    private final FunctionService functionService;

    private final ViewService viewService;

    private final SPipesExecutionService executionService;

    @Autowired
    public ExecutionController(FunctionService functionService, SPipesExecutionService executionService, ViewService viewService) {
        this.functionService = functionService;
        this.executionService = executionService;
        this.viewService = viewService;
    }

    @GetMapping(path = "/history")
    public List<ExecutionDTO> historyOfAllExecution() {
        return executionService.getAllExecution();
    }

    @PostMapping(path = "/history-modules")
    public Set<ModuleExecutionInfo> historyOfModule(@RequestBody ScriptDTO scriptDTO) {
        String transformationId = scriptDTO.getTransformationId();
        return viewService.modulesExecutionInfo(transformationId);
    }

    @GetMapping(path = "/{executionId}")
    public String getPipelineName(@PathVariable("executionId") String executionId) {
        return executionService.getPipelineName(executionId);

    }

}
