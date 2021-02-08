package og_spipes.rest;

import cz.cvut.kbss.jsonld.JsonLd;
import og_spipes.model.dto.ScriptDTO;
import og_spipes.model.filetree.SubTree;
import og_spipes.model.spipes.ModuleType;
import og_spipes.model.spipes.TestJSONLD;
import og_spipes.service.FileTreeService;
import og_spipes.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.lang.reflect.Array;
import java.util.List;

@RestController
@RequestMapping("/scripts")
public class ScriptController {

    @Value("${repositoryUrl}")
    private String repositoryURL;

    private final FileTreeService fileTreeService;
    private final ScriptService scriptService;

    @Autowired
    public ScriptController(FileTreeService fileTreeService, ScriptService scriptService) {
        this.fileTreeService = fileTreeService;
        this.scriptService = scriptService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public SubTree getScripts() {
        //TODO get direct root
        return fileTreeService.getTtlFileTree(new File(repositoryURL));
    }
    @PostMapping(path = "/moduleTypes", produces = JsonLd.MEDIA_TYPE)
    public List<ModuleType> getModuleTypes(@RequestBody ScriptDTO dto) {
        String script = dto.getAbsolutePath();
        List<ModuleType> moduleTypes = scriptService.getModuleTypes(script);
        System.out.println("sizu controller: " + moduleTypes.size());

        //WHY??? - cz/cvut/kbss/jopa/model/MultilingualString
        return moduleTypes;
    }

}
