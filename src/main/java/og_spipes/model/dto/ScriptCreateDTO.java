package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.sforms.model.AbstractEntity;
import og_spipes.model.Vocabulary;

import static og_spipes.model.Vocabulary.*;

@MappedSuperclass
@OWLClass(iri = Vocabulary.s_c_script_create_dto)
public class ScriptCreateDTO extends AbstractEntity {

    @OWLDataProperty(iri = s_p_has_script_path)
    private String directoryPath;

    @OWLDataProperty(iri = s_p_has_name)
    private String name;

    @OWLDataProperty(iri = s_p_has_ontology_uri)
    private String ontologyUri;

    public ScriptCreateDTO() {
    }

    public ScriptCreateDTO(String directoryPath, String name, String ontologyUri) {
        this.directoryPath = directoryPath;
        this.name = name;
        this.ontologyUri = ontologyUri;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "directoryPath='" + directoryPath + '\'' +
                ", name='" + name + '\'' +
                ", ontologyUri='" + ontologyUri + '\'' +
                '}';
    }
}

