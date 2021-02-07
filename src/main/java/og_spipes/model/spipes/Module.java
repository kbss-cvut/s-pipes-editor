package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.*;
import og_spipes.model.AbstractEntitySP;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import static og_spipes.model.Vocabulary.*;

@OWLClass(iri = s_c_Modules)
public class Module extends AbstractEntitySP {

    @OWLDataProperty(iri = s_p_label)
    private String label;
    @OWLObjectProperty(iri = s_p_next, fetch = FetchType.EAGER)
    private Set<Module> next;

    @OWLObjectProperty(iri = s_p_specific_type)
    private ModuleType specificType;

    @Types
    private Set<String> types;

    public Module() {
    }

    public Module(String label, Set<Module> next) {
        this.id = UUID.randomUUID().toString();
        this.uri = URI.create(s_c_Modules + "/" + id);
        this.label = label;
        this.next = next;
    }

    public Module(URI uri, String id, String label, Set<Module> next) {
        this.uri = uri;
        this.id = id;
        this.label = label;
        this.next = next;
    }

    public Module(URI uri, String id, String label, Set<Module> next, ModuleType specificType, Set<String> types) {
        this.uri = uri;
        this.id = id;
        this.label = label;
        this.next = next;
        this.specificType = specificType;
        this.types = types;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<Module> getNext() {
        return next;
    }

    public void setNext(Set<Module> next) {
        this.next = next;
    }

    public Set<String> getTypes() {
        return types;
    }

    public void setTypes(Set<String> types) {
        this.types = types;
    }

    public ModuleType getSpecificType() {
        return specificType;
    }

    public void setSpecificType(ModuleType specificType) {
        this.specificType = specificType;
    }
}
