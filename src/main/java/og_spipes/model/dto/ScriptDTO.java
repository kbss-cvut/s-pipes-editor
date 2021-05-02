package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.sforms.model.AbstractEntity;
import og_spipes.model.Vocabulary;

import static og_spipes.model.Vocabulary.*;

@MappedSuperclass
@OWLClass(iri = Vocabulary.s_c_script_dto)
public class ScriptDTO extends AbstractEntity {

    @OWLDataProperty(iri = s_p_has_script_path)
    private String scriptPath;

    @OWLDataProperty(iri = s_p_has_absolute_path)
    private String absolutePath;

    @OWLDataProperty(iri = s_p_has_transformation_id)
    private String transformationId;

    public ScriptDTO() {
    }

    public ScriptDTO(String scriptPath, String absolutePath, String executionId) {
        this.scriptPath = scriptPath;
        this.absolutePath = absolutePath;
        this.transformationId = executionId;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getTransformationId() {
        return transformationId;
    }

    public void setTransformationId(String transformationId) {
        this.transformationId = transformationId;
    }
}
