package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;

import static og_spipes.model.Vocabulary.s_c_module_dto;
import static og_spipes.model.Vocabulary.s_p_has_module_uri;

@MappedSuperclass
@OWLClass(iri = s_c_module_dto)
public class ModuleDTO extends ScriptDTO {

    @OWLDataProperty(iri = s_p_has_module_uri)
    private String moduleUri;

    public String getModuleUri() {
        return moduleUri;
    }

    public void setModuleUri(String moduleUri) {
        this.moduleUri = moduleUri;
    }
}
