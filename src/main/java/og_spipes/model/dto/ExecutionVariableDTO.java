package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.sforms.model.AbstractEntity;
import og_spipes.model.Vocabulary;

import static og_spipes.model.Vocabulary.*;

@OWLClass(iri = Vocabulary.s_c_execution_variable_dto)
public class ExecutionVariableDTO extends AbstractEntity {

    @OWLDataProperty(iri = s_p_has_variable_name)
    private String variableName;

    @OWLDataProperty(iri = s_p_has_variable_value)
    private String variableValue;

    public ExecutionVariableDTO() {
    }

    public ExecutionVariableDTO(String variableName, String variableValue) {
        this.variableName = variableName;
        this.variableValue = variableValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    @Override
    public String toString() {
        return "ExecutionVariableDTO{" +
                "variableName='" + variableName + '\'' +
                ", variableValue='" + variableValue + '\'' +
                '}';
    }
}

