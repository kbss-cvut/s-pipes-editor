package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.sforms.model.AbstractEntity;

import og_spipes.model.Vocabulary;
import static og_spipes.model.Vocabulary.*;

@OWLClass(iri = Vocabulary.s_c_script_function_argument)

public class ScriptFunctionArgument extends AbstractEntity {

    @OWLDataProperty(iri = s_p_has_function_argument_name)
    private String name;
    @OWLDataProperty(iri = s_p_has_function_argument_label)

    private String label;
    @OWLDataProperty(iri = s_p_has_function_argument_comment)
    private String comment;


    public ScriptFunctionArgument() {
    }

    public ScriptFunctionArgument(String name, String label, String comment) {
        this.name = name;
        this.label = label;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "FunctionArgument{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
