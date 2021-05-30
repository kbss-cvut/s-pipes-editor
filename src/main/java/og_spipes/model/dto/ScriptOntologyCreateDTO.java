package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.sforms.model.AbstractEntity;
import og_spipes.model.Vocabulary;

import static og_spipes.model.Vocabulary.*;

@MappedSuperclass
@OWLClass(iri = Vocabulary.s_c_script_ontology_create_dto)
public class ScriptOntologyCreateDTO extends AbstractEntity {

    @OWLDataProperty(iri = s_p_has_script_path)
    private String scriptPath;

    @OWLDataProperty(iri = s_p_has_ontology_uri)
    private String ontologyUri;

    public ScriptOntologyCreateDTO() {
    }

    public ScriptOntologyCreateDTO(String scriptPath, String ontologyUri) {
        this.scriptPath = scriptPath;
        this.ontologyUri = ontologyUri;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getOntologyUri() {
        return ontologyUri;
    }

    public void setOntologyUri(String ontologyUri) {
        this.ontologyUri = ontologyUri;
    }

    @Override
    public String toString() {
        return "ScriptCreateDTO{" +
                "scriptPath='" + scriptPath + '\'' +
                ", ontologyUri='" + ontologyUri + '\'' +
                '}';
    }
}

