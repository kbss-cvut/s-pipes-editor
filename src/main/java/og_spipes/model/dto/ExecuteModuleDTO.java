package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.sforms.model.AbstractEntity;
import og_spipes.model.Vocabulary;

import static og_spipes.model.Vocabulary.*;

@MappedSuperclass
@OWLClass(iri = Vocabulary.s_c_execution_module_dto)
public class ExecuteModuleDTO extends AbstractEntity {

    @OWLDataProperty(iri = s_p_has_script_path)
    private String scriptPath;

    @OWLDataProperty(iri = s_p_has_module_uri)
    private String moduleURI;

    @OWLDataProperty(iri = s_p_has_input_parameter)
    private String moduleInput;

    //JOPA does not support Map<String, String> for the params
    @OWLDataProperty(iri = s_p_has_parameter)
    private String params;

    public ExecuteModuleDTO() {
    }

    public ExecuteModuleDTO(String scriptPath, String moduleURI, String moduleInput, String params) {
        this.scriptPath = scriptPath;
        this.moduleURI = moduleURI;
        this.moduleInput = moduleInput;
        this.params = params;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getModuleURI() {
        return moduleURI;
    }

    public void setModuleURI(String moduleURI) {
        this.moduleURI = moduleURI;
    }

    public String getModuleInput() {
        return moduleInput;
    }

    public void setModuleInput(String moduleInput) {
        this.moduleInput = moduleInput;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "ExecuteModuleDTO{" +
                "scriptPath='" + scriptPath + '\'' +
                ", moduleURI='" + moduleURI + '\'' +
                ", moduleInput='" + moduleInput + '\'' +
                ", params='" + params + '\'' +
                '}';
    }
}
