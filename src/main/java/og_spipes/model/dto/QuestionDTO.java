package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.cvut.sforms.model.Question;

import static og_spipes.model.Vocabulary.*;

@OWLClass(iri = s_c_question_dto)
public class QuestionDTO extends ModuleDTO {
    @OWLDataProperty(iri = s_p_has_module_type_uri)
    private String moduleTypeUri;

    @OWLObjectProperty(iri = s_p_has_root_question)
    private Question rootQuestion;

    @OWLObjectProperty(iri = s_p_has_execution_script_path)
    private String executionScriptPath;

    public String getExecutionScriptPath() {
        return executionScriptPath != null && !executionScriptPath.isEmpty() ? executionScriptPath : getScriptPath();
    }

    public String getModuleTypeUri() {
        return moduleTypeUri;
    }

    public void setModuleTypeUri(String moduleTypeUri) {
        this.moduleTypeUri = moduleTypeUri;
    }

    public Question getRootQuestion() {
        return rootQuestion;
    }

    public void setRootQuestion(Question rootQuestion) {
        this.rootQuestion = rootQuestion;
    }
}
