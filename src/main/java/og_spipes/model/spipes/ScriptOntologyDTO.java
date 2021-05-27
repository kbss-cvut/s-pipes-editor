package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import og_spipes.model.AbstractEntitySP;

import static og_spipes.model.Vocabulary.*;

@OWLClass(iri = s_c_script_ontology_dto)
public class ScriptOntologyDTO extends AbstractEntitySP {

    @OWLDataProperty(iri = s_p_has_script_path)
    private String scriptPath;

    @OWLDataProperty(iri = s_p_has_ontology_uri)
    private String ontologyUri;

    public ScriptOntologyDTO() {
    }

    public ScriptOntologyDTO(String scriptPath, String ontologyUri) {
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
}
