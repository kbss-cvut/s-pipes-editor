package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.sforms.model.AbstractEntity;
import og_spipes.model.Vocabulary;

import static og_spipes.model.Vocabulary.*;

@MappedSuperclass
@OWLClass(iri = Vocabulary.s_c_move_module_dto)
public class MoveModuleDTO extends AbstractEntity {

    @OWLDataProperty(iri = s_p_has_module_from_path)
    private String moduleFromPath;

    @OWLDataProperty(iri = s_p_has_module_to_path)
    private String moduleToPath;

    @OWLDataProperty(iri = s_p_has_module_uri)
    private String moduleUri;

    @OWLDataProperty(iri = s_p_rename_module)
    private String renameModule;

    public MoveModuleDTO() {
    }

    public MoveModuleDTO(String moduleFromPath, String moduleToPath, String moduleUri, String renameModule) {
        this.moduleFromPath = moduleFromPath;
        this.moduleToPath = moduleToPath;
        this.moduleUri = moduleUri;
        this.renameModule = renameModule;
    }

    public String getModuleFromPath() {
        return moduleFromPath;
    }

    public void setModuleFromPath(String moduleFromPath) {
        this.moduleFromPath = moduleFromPath;
    }

    public String getModuleToPath() {
        return moduleToPath;
    }

    public void setModuleToPath(String moduleToPath) {
        this.moduleToPath = moduleToPath;
    }

    public String getModuleUri() {
        return moduleUri;
    }

    public void setModuleUri(String moduleUri) {
        this.moduleUri = moduleUri;
    }

    public String getRenameModule() {
        return renameModule;
    }

    public void setRenameModule(String renameModule) {
        this.renameModule = renameModule;
    }

    @Override
    public String toString() {
        return "MoveModuleDTO{" +
                "moduleFromPath='" + moduleFromPath + '\'' +
                ", moduleToPath='" + moduleToPath + '\'' +
                ", moduleUri='" + moduleUri + '\'' +
                ", renameModule='" + renameModule + '\'' +
                '}';
    }
}
