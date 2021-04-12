package og_spipes.rest;

import og_spipes.service.FunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/execution")
public class ExecutionController {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionController.class);

    private final FunctionService functionService;

    @Autowired
    public ExecutionController(FunctionService functionService) {
        this.functionService = functionService;
    }

    @PostMapping(path = "/function")
    public void executeFunction(@RequestBody String function) {

    }

    @PostMapping(path = "/module")
    public void executeModule(@RequestBody String input) {

    }

    @GetMapping(path = "/history")
    public String historyOfExecution(@RequestBody String executionId) {
        return null;
    }

    @PostMapping(path = "/history-module")
    public void historyOfModule(@RequestBody String executionId, String moduleId) {

    }

}
