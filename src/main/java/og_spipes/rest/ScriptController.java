package og_spipes.rest;

import cz.cvut.kbss.jsonld.JsonLd;
import og_spipes.model.dto.ModuleDTO;
import og_spipes.model.dto.SHACLValidationResultDTO;
import og_spipes.model.dto.ScriptDTO;
import og_spipes.model.filetree.SubTree;
import og_spipes.model.spipes.DependencyDTO;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.spipes.ScriptOntologyDTO;
import og_spipes.service.FileTreeService;
import og_spipes.service.OntologyHelper;
import og_spipes.service.SHACLExecutorService;
import og_spipes.service.ScriptService;
import og_spipes.service.util.ScriptImportGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.topbraid.shacl.validation.SHACLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/scripts")
public class ScriptController {

    @Value("${scriptPaths}")
    private String[] scriptPaths;

    @Value("${scriptRules}")
    private String scriptRules;

    private final FileTreeService fileTreeService;
    private final ScriptService scriptService;
    private final SHACLExecutorService executorService;

    @Autowired
    public ScriptController(FileTreeService fileTreeService, ScriptService scriptService, SHACLExecutorService executorService) {
        this.fileTreeService = fileTreeService;
        this.scriptService = scriptService;
        this.executorService = executorService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public SubTree getScripts() {
        //TODO get direct root
        File[] scripts = Arrays.stream(scriptPaths).map(File::new).toArray(File[]::new);
        return fileTreeService.getTtlFileTree(scripts);
    }

    @PostMapping(path = "/ontologies", produces = JsonLd.MEDIA_TYPE)
    public List<ScriptOntologyDTO> getOntologies(@RequestBody ScriptDTO dto) {
        String script = dto.getAbsolutePath();
        ScriptImportGroup importGroup = new ScriptImportGroup(scriptPaths, new File(script));

        return importGroup.getUsedFiles().stream().map(f ->{
            String ontologyUri = OntologyHelper.getOntologyUri(f);
            return new ScriptOntologyDTO(f.getAbsolutePath(), ontologyUri);
        }).collect(Collectors.toList());
    }

    @PostMapping(path = "/moduleTypes", produces = JsonLd.MEDIA_TYPE)
    public List<ModuleType> getModuleTypes(@RequestBody ScriptDTO dto) {
        String script = dto.getAbsolutePath();

        return scriptService.getModuleTypes(script);
    }

    @PostMapping(path = "/modules/dependency")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void createDependency(@RequestBody DependencyDTO dto) throws FileNotFoundException {
        String script = dto.getAbsolutePath();
        scriptService.createDependency(script, dto.getModuleUri(), dto.getTargetModuleUri());
    }

    @PostMapping(path = "/modules/delete")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteModule(@RequestBody ModuleDTO dto) throws FileNotFoundException {
        String scriptPath = dto.getAbsolutePath();
        String moduleUri = dto.getModuleUri();
        scriptService.deleteModule(scriptPath, moduleUri);
    }

    @PostMapping(path = "/modules/dependencies/delete")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteDependency(@RequestBody DependencyDTO dto) throws FileNotFoundException {
        String scriptPath = dto.getAbsolutePath();
        scriptService.deleteDependency(scriptPath, dto.getModuleUri(), dto.getTargetModuleUri());
    }

    @PostMapping(path = "/validate", produces = JsonLd.MEDIA_TYPE)
    public Set<SHACLValidationResultDTO> validateScript(@RequestBody ScriptDTO dto) throws IOException, URISyntaxException {
        List<File> rules = Files.walk(new File(scriptRules).toPath())
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
        Set<SHACLValidationResultDTO> violations = new HashSet<>();
        for(File f : rules){
            violations.addAll(
                    executorService.testModel(
                        Collections.singleton(f.toURI().toURL()),
                        dto.getAbsolutePath()
                    )
            );
        }
        return violations;
    }
}
