package og_spipes.rest;

import cz.cvut.kbss.jsonld.JsonLd;
import cz.cvut.sforms.model.Question;
import og_spipes.model.dto.ExecuteFunctionDTO;
import og_spipes.model.dto.ModuleDTO;
import og_spipes.model.dto.QuestionDTO;
import og_spipes.model.dto.ScriptDTO;
import og_spipes.model.filetree.SubTree;
import og_spipes.model.spipes.DependencyDTO;
import og_spipes.model.spipes.FunctionDTO;
import og_spipes.model.spipes.ModuleType;
import og_spipes.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
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
    public String executeFunction(@RequestBody ExecuteFunctionDTO dto) {
        LOG.info(dto.toString());

        String function = dto.getFunction();
        String[] split = dto.getParams().split("&");

        Map<String, String> params = new HashMap<>();
        if(!dto.getParams().equals("")){
            params = Arrays.stream(split)
                    .map(elem -> elem.split("="))
                    .collect(Collectors.toMap(e -> e[0], e -> e[1]));
        }

        return executorService.serviceExecution(function, params);
    }

}
