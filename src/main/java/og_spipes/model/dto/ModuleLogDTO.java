package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.sforms.model.AbstractEntity;
import og_spipes.model.Vocabulary;

import java.util.List;
import java.util.Set;

import static og_spipes.model.Vocabulary.*;

@MappedSuperclass
@OWLClass(iri = s_c_module_log_dto)
public class ModuleLogDTO extends AbstractEntity {

    @OWLObjectProperty(iri = s_p_has_absolute_path, fetch = FetchType.EAGER)
    private Set<String> logPath;

    public ModuleLogDTO() {
    }

    public ModuleLogDTO(Set<String> logPath) {
        this.logPath = logPath;
    }

    public Set<String> getLogPath() {
        return logPath;
    }

    public void setLogPath(Set<String> logPath) {
        this.logPath = logPath;
    }

    @Override
    public String toString() {
        return "ModuleLogDTO{" +
                "logPath=" + logPath +
                ", uri=" + uri +
                '}';
    }
}
