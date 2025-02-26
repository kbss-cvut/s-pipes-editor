package og_spipes.rest;

import cz.cvut.kbss.jsonld.JsonLd;
import cz.cvut.kbss.jsonld.exception.TargetTypeException;
import og_spipes.config.Constants;
import og_spipes.model.dto.*;
import og_spipes.model.filetree.SubTree;
import og_spipes.model.spipes.DependencyDTO;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.spipes.ScriptOntologyDTO;
import og_spipes.model.view.ErrorMessage;
import og_spipes.persistence.dao.OntologyDao;
import og_spipes.service.FileTreeService;
import og_spipes.service.SHACLExecutorService;
import og_spipes.service.ScriptService;
import og_spipes.service.exception.FileExistsException;
import og_spipes.service.exception.MissingOntologyException;
import og_spipes.service.exception.OntologyDuplicationException;
import og_spipes.service.util.ScriptImportGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/scripts")
public class ScriptController {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptController.class);

    @Value(Constants.SCRIPTPATH_SPEL)
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

    /**
     * Basic error handling
     * @param exception - Covered exceptions
     * @return - Error message
     */
    @ResponseBody
    @ExceptionHandler({ NullPointerException.class, OntologyDuplicationException.class, URISyntaxException.class, FileExistsException.class, TargetTypeException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleException(Exception exception) {
        LOG.error("Error ScriptController: ", exception);
        return new ErrorMessage(exception.getMessage());
    }

    @RequestMapping(method = RequestMethod.GET)
    public SubTree getScripts() {
        File[] scripts = Arrays.stream(scriptPaths).map(File::new).toArray(File[]::new);
        return fileTreeService.getTtlFileTree(scripts);
    }

    @PostMapping(path = "/create", produces = JsonLd.MEDIA_TYPE)
    public void createScript(@RequestBody ScriptCreateDTO dto) throws IOException, OntologyDuplicationException, URISyntaxException, FileExistsException {
        LOG.info("Create script: " + dto);
        URI ontologyURI = new URI(dto.getOntologyUri());
        scriptService.createScript(dto.getDirectoryPath(), dto.getName(), dto.getType(), ontologyURI,
                dto.getFunctionPrefix());
    }

    @PostMapping(path = "/delete", produces = JsonLd.MEDIA_TYPE)
    public void deleteScript(@RequestBody ScriptDTO dto) {
        String script = dto.getAbsolutePath();
        LOG.info("Delete script: " + script);
        File file = new File(script);
        FileSystemUtils.deleteRecursively(file);
    }

    @PostMapping(path = "/ontologies", produces = JsonLd.MEDIA_TYPE)
    public List<ScriptOntologyDTO> getOntologies(@RequestBody ScriptDTO dto) {
        String script = dto.getAbsolutePath();
        ScriptImportGroup importGroup = new ScriptImportGroup(scriptPaths, new File(script));

        return importGroup.getUsedFiles().stream().map(f ->{
            String ontologyUri = OntologyDao.getOntologyUri(f);
            return new ScriptOntologyDTO(f.getAbsolutePath(), ontologyUri);
        }).collect(Collectors.toList());
    }

    //rename to all-ontolgoies
    @PostMapping(path = "/moduleTypes", produces = JsonLd.MEDIA_TYPE)
    public List<ModuleType> getModuleTypes(@RequestBody ScriptDTO dto) {
        String script = dto.getAbsolutePath();

        return scriptService.getModuleTypes(script);
    }

    @PostMapping(path = "/modules/move")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void moveModule(@RequestBody MoveModuleDTO dto) throws IOException {
        LOG.info(dto.toString());
        boolean rename = Boolean.parseBoolean(dto.getRenameModule());
        scriptService.moveModule(
                dto.getModuleFromPath(),
                dto.getModuleToPath(),
                dto.getModuleUri(),
                rename
        );
    }

    @PostMapping(path = "/modules/dependency")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void createDependency(@RequestBody DependencyDTO dto) throws IOException {
        String script = dto.getAbsolutePath();
        scriptService.createDependency(script, dto.getModuleUri(), dto.getTargetModuleUri());
    }

    @PostMapping(path = "/modules/delete")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteModule(@RequestBody ModuleDTO dto) throws IOException {
        String scriptPath = dto.getAbsolutePath();
        String moduleUri = dto.getModuleUri();
        scriptService.deleteModule(scriptPath, moduleUri);
    }

    @PostMapping(path = "/modules/dependencies/delete")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteDependency(@RequestBody DependencyDTO dto) throws IOException {
        String scriptPath = dto.getAbsolutePath();
        scriptService.deleteDependency(scriptPath, dto.getModuleUri(), dto.getTargetModuleUri());
    }

    @PostMapping(path = "/validate", produces = JsonLd.MEDIA_TYPE)
    public Set<SHACLValidationResultDTO> validateScript(@RequestBody ScriptDTO dto) throws IOException, URISyntaxException {
        List<File> rules = Files.walk(new File(scriptRules).toPath())
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
        Set<SHACLValidationResultDTO> violations = executorService.testModel(rules, dto.getAbsolutePath());
        return violations;
    }

    @PostMapping(path = "/ontology/remove", produces = JsonLd.MEDIA_TYPE)
    public void removeScriptOntology(@RequestBody ScriptOntologyCreateDTO dto) throws IOException {
        LOG.info(dto.toString());
        scriptService.removeScriptOntology(
                dto.getScriptPath(),
                dto.getOntologyUri()
        );
    }

    @PostMapping(path = "/ontology/add", produces = JsonLd.MEDIA_TYPE)
    public void addModuleOntology(@RequestBody ScriptOntologyCreateDTO dto) throws IOException, MissingOntologyException {
        scriptService.addScriptOntology(
                dto.getScriptPath(),
                dto.getOntologyUri()
        );
    }

    @PostMapping(path = "/own-ontology", produces = JsonLd.MEDIA_TYPE)
    public List<String> listScriptOntology(@RequestBody ScriptDTO dto) {
        return scriptService.getScriptImportedOntologies(
                dto.getAbsolutePath()
        );
    }

}
