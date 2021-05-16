package og_spipes.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import og_spipes.model.AbstractEntitySP;
import og_spipes.model.Vocabulary;

import static og_spipes.model.Vocabulary.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@OWLClass(iri = Vocabulary.s_c_shacl_validation_result_dto)
public class SHACLValidationResultDTO extends AbstractEntitySP {

    @OWLDataProperty(iri = s_p_has_module_uri)
    private String moduleURI;

    @OWLDataProperty(iri = s_p_has_severity_message)
    private String severityMessage;

    @OWLDataProperty(iri = s_p_has_error_message)
    private String errorMessage;

    @OWLDataProperty(iri = s_p_has_rule_uri)
    private String ruleURI;

    public SHACLValidationResultDTO() {
    }

    public SHACLValidationResultDTO(String moduleURI, String severityMessage, String errorMessage, String ruleURI) {
        this.moduleURI = moduleURI;
        this.severityMessage = severityMessage;
        this.errorMessage = errorMessage;
        this.ruleURI = ruleURI;
    }

    public String getModuleURI() {
        return moduleURI;
    }

    public void setModuleURI(String moduleURI) {
        this.moduleURI = moduleURI;
    }

    public String getSeverityMessage() {
        return severityMessage;
    }

    public void setSeverityMessage(String severityMessage) {
        this.severityMessage = severityMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRuleURI() {
        return ruleURI;
    }

    public void setRuleURI(String ruleURI) {
        this.ruleURI = ruleURI;
    }

    @Override
    public String toString() {
        return "SHACLValidationResultDTO{" +
                "moduleURI='" + moduleURI + '\'' +
                ", severityMessage='" + severityMessage + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", ruleURI='" + ruleURI + '\'' +
                '}';
    }
}