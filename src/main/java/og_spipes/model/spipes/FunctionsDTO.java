package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.*;
import og_spipes.model.AbstractEntitySP;

import java.util.List;

import static og_spipes.model.Vocabulary.*;

@OWLClass(iri = s_c_functions_dto)
public class FunctionsDTO extends AbstractEntitySP {

    @OWLDataProperty(iri = s_p_has_script_path)
    private String scriptPath;

    @Sequence
    @OWLObjectProperty(iri = s_p_has_function_dto, fetch = FetchType.EAGER)
    private List<FunctionDTO> functions;

    public FunctionsDTO() {
    }

    public FunctionsDTO(String scriptPath, List<FunctionDTO> functions) {
        this.scriptPath = scriptPath;
        this.functions = functions;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public List<FunctionDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionDTO> functions) {
        this.functions = functions;
    }
}