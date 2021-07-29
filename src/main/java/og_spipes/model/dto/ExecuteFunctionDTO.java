package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.sforms.model.AbstractEntity;
import og_spipes.model.Vocabulary;

import java.util.Map;
import java.util.Set;

import static og_spipes.model.Vocabulary.*;

@MappedSuperclass
@OWLClass(iri = Vocabulary.s_c_execution_function_dto)
public class ExecuteFunctionDTO extends AbstractEntity {

    @OWLDataProperty(iri = s_p_has_script_path)
    private String scriptPath;

    @OWLDataProperty(iri = s_p_has_function_uri)
    private String function;

    //JOPA does not support Map<String, String> for the params
    @OWLDataProperty(iri = s_p_has_parameter)
    private String params;

    public ExecuteFunctionDTO() {
    }

    public ExecuteFunctionDTO(String scriptPath, String function, String params) {
        this.scriptPath = scriptPath;
        this.function = function;
        this.params = params;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "ExecuteFunctionDTO{" +
                "scriptPath='" + scriptPath + '\'' +
                ", function='" + function + '\'' +
                ", params='" + params + '\'' +
                '}';
    }
}
