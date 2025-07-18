package og_spipes.rest;

import cz.cvut.kbss.jsonld.JsonLd;
import cz.cvut.kbss.jsonld.exception.TargetTypeException;
import cz.cvut.sforms.model.Question;
import og_spipes.model.dto.*;
import og_spipes.model.filetree.SubTree;
import og_spipes.model.spipes.DependencyDTO;
import og_spipes.model.spipes.FunctionDTO;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.view.ErrorMessage;
import og_spipes.service.*;
import og_spipes.service.exception.FileExistsException;
import og_spipes.service.exception.OntologyDuplicationException;
import og_spipes.service.exception.SPipesEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/function")
public class FunctionController {

    private static final Logger LOG = LoggerFactory.getLogger(FunctionController.class);

    private final FunctionService functionService;

    private final SPipesExecutionService executorService;

    private final FormService formService;

    @Autowired
    public FunctionController(FunctionService functionService, SPipesExecutionService executorService, FormService formService) {
        this.functionService = functionService;
        this.executorService = executorService;
        this.formService = formService;
    }

    @ResponseBody
    @ExceptionHandler({ IOException.class, SPipesEngineException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleException(Exception exception) {
        LOG.error("Error ScriptController: ", exception);
        return new ErrorMessage(exception.getMessage());
    }

    @PostMapping(path = "/script", produces = JsonLd.MEDIA_TYPE)
    public List<FunctionDTO> getScriptFunctions(@RequestBody ScriptDTO dto) {
        return functionService.moduleFunctions(dto.getAbsolutePath());
    }

    @PostMapping(path = "/form")
    public Question generateModuleForm(@RequestBody ExecuteFunctionDTO dto) {
        LOG.info("Generating form for function " + dto.getScriptPath() + ", function " + dto.getFunction());
        return formService.generateFunctionForm(dto.getScriptPath(), dto.getFunction());
    }

    @PostMapping(path = "/execute", produces = JsonLd.MEDIA_TYPE)
    public String executeFunction(@RequestBody ExecuteFunctionDTO dto) throws SPipesEngineException {
        LOG.info(dto.toString());

        String function = dto.getFunction();
        String[] split = dto.getParams().split("&");
        String scriptPath = dto.getScriptPath();

        Map<String, String> params = new HashMap<>();
        if(!dto.getParams().equals("")){
            params = Arrays.stream(split)
                    .map(elem -> elem.split("="))
                    .collect(Collectors.toMap(e -> e[0], e -> e[1]));
        }

        return executorService.serviceExecution(function, params, scriptPath);
    }

    @PostMapping(path = "/module/execute")
    public String executeModule(@RequestBody ExecuteModuleDTO dto) throws IOException, SPipesEngineException {
        LOG.info("Module execution DTO: " + dto.toString());

        Map<String, String> params = new HashMap<>();
        if(dto.getParams() != null && !dto.getParams().equals("")){
            String[] split = dto.getParams().split("&");
            params = Arrays.stream(split)
                    .map(elem -> elem.split("="))
                    .collect(Collectors.toMap(e -> e[0], e -> e[1]));
        }

        String execution = executorService.moduleExecution(
                dto.getScriptPath(),
                dto.getModuleInput(),
                dto.getModuleURI(),
                params
        );
        LOG.info("Module execution message: " + execution);
        return execution;
    }

}
