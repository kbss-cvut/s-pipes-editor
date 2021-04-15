package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.*;
import og_spipes.model.AbstractEntitySP;

import java.util.Set;

import static og_spipes.model.Vocabulary.*;

@OWLClass(iri = s_c_function_dto)
public class FunctionDTO extends AbstractEntitySP {

    @OWLDataProperty(iri = s_p_has_function_local_name)
    private String functionLocalName;

    @OWLDataProperty(iri = s_p_has_function_uri)
    private String functionUri;

    @OWLDataProperty(iri = s_p_comment)
    private Set<String> comment;

    public FunctionDTO() {
    }

    public FunctionDTO(String functionUri, String functionLocalName, Set<String> comment) {
        this.functionLocalName = functionLocalName;
        this.functionUri = functionUri;
        this.comment = comment;
    }

    public String getFunctionLocalName() {
        return functionLocalName;
    }

    public void setFunctionLocalName(String functionLocalName) {
        this.functionLocalName = functionLocalName;
    }

    public String getFunctionUri() {
        return functionUri;
    }

    public void setFunctionUri(String functionUri) {
        this.functionUri = functionUri;
    }

    public Set<String> getComment() {
        return comment;
    }

    public void setComment(Set<String> comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "FunctionDTO{" +
                "functionLocalName='" + functionLocalName + '\'' +
                ", functionUri='" + functionUri + '\'' +
                ", comment=" + comment +
                '}';
    }
}

