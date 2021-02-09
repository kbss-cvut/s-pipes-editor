package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import og_spipes.model.dto.ModuleDTO;

import static og_spipes.model.Vocabulary.s_c_dependency_dto;
import static og_spipes.model.Vocabulary.s_p_has_target_module_uri;

@OWLClass(iri = s_c_dependency_dto)
public class DependencyDTO extends ModuleDTO {

    @OWLDataProperty(iri = s_p_has_target_module_uri)
    private String targetModuleUri;

    public String getTargetModuleUri() {
        return targetModuleUri;
    }

    public void setTargetModuleUri(String targetModuleUri) {
        this.targetModuleUri = targetModuleUri;
    }
}
