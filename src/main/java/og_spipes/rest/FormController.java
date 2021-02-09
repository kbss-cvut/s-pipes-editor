package og_spipes.rest;

import cz.cvut.sforms.model.Question;
import og_spipes.model.dto.QuestionDTO;
import og_spipes.service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//TODO rename - but follow the previous project
@RequestMapping("/scripts")
public class FormController {

    private static final Logger LOG = LoggerFactory.getLogger(FormController.class);

    private final FormService formService;

    @Autowired
    public FormController(FormService formService) {
        this.formService = formService;
    }

    //TODO TEST- quite hard due to massive question class
    @PostMapping(path = "/forms")
    public Question generateModuleForm(@RequestBody QuestionDTO requestDTO) {
        String script = requestDTO.getScriptPath();
        LOG.info("Generating form for script " + script + ", module " + requestDTO.getModuleUri() + " of type " + requestDTO.getModuleTypeUri());
        return formService.generateModuleForm(
                script,
                requestDTO.getModuleUri(),
                requestDTO.getModuleTypeUri()
        );
    }

}
