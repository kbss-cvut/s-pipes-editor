package og_spipes.rest;

import og_spipes.model.dto.ScriptDTO;
import og_spipes.model.view.View;
import og_spipes.service.ViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/views")
public class ViewController {

    private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);

    private final ViewService viewService;

    @Autowired
    public ViewController(ViewService viewService) {
        this.viewService = viewService;
    }

    @PostMapping(path = "/new")
    public View generateModuleForm(@RequestBody ScriptDTO scriptDTO) {
        String script = scriptDTO.getScriptPath();
        String transformationId = scriptDTO.getTransformationId();
        LOG.info("Creating a view for script " + script + ", with transformationId: " + transformationId);
        return viewService.newViewFromSpipes(script, transformationId);
    }

}
