package og_spipes.rest;

import cz.cvut.kbss.jsonld.JsonLd;
import og_spipes.model.dto.ExecuteFunctionDTO;
import og_spipes.model.dto.ModuleDTO;
import og_spipes.model.dto.ScriptDTO;
import og_spipes.model.filetree.SubTree;
import og_spipes.model.spipes.DependencyDTO;
import og_spipes.model.spipes.FunctionDTO;
import og_spipes.model.spipes.ModuleType;
import og_spipes.service.FileTreeService;
import og_spipes.service.FunctionService;
import og_spipes.service.SPipesExecutionService;
import og_spipes.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/function")
public class FunctionController {

    private final FunctionService functionService;

    private final SPipesExecutionService executorService;

    @Autowired
    public FunctionController(FunctionService functionService, SPipesExecutionService executorService) {
        this.functionService = functionService;
        this.executorService = executorService;
    }

    @PostMapping(path = "/script", produces = JsonLd.MEDIA_TYPE)
    public List<FunctionDTO> getScriptFunctions(@RequestBody ScriptDTO dto) {
        System.out.println(functionService.hashCode());
        return functionService.moduleFunctions(dto.getAbsolutePath());
    }

    @PostMapping(path = "/execute", produces = JsonLd.MEDIA_TYPE)
    public String executeFunction(@RequestBody ExecuteFunctionDTO dto) {
        //TODO only BindWithConstant are input params? - how to pass them?
        //TODO should I use S-Forms?
        System.out.println(dto);

        String function = dto.getFunction();
        String[] split = dto.getParams().split("&");

        //TODO invalid params check
        Map<String, String> params = Arrays.stream(split)
                        .map(elem -> elem.split("="))
                        .collect(Collectors.toMap(e -> e[0], e -> e[1]));

        return executorService.serviceExecution(function, params);
    }

}
