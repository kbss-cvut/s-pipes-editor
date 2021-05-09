package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.*;
import og_spipes.model.AbstractEntitySP;

import java.net.URI;
import java.util.Map;
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

    @Properties
    private Map<String, Set<Object>> properties;

    @OWLDataProperty(iri = s_p_node_x)
    private String nodeX;

    @OWLDataProperty(iri = s_p_node_y)
    private String nodeY;

    @OWLDataProperty(iri = s_p_source)
    private String source;

    public Module() {
    }

    public Module(String label, Set<Module> next) {
        this.id = UUID.randomUUID().toString();
        this.uri = URI.create(s_c_Modules + "/" + id);
        this.label = label;
        this.next = next;
    }

//    public Module(URI uri, String id, String label, Set<Module> next, ModuleType specificType, Set<String> types, String nodeX, String nodeY, String group) {
    public Module(URI uri, String id, String label, Set<Module> next, ModuleType specificType, Set<String> types, Map<String, Set<Object>> properties, String nodeX, String nodeY, String source) {
        this.uri = uri;
        this.id = id;
        this.label = label;
        this.next = next;
        this.specificType = specificType;
        this.types = types;
        this.properties = properties;
        this.nodeX = nodeX;
        this.nodeY = nodeY;
        this.source = source;
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

    public Map<String, Set<Object>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Set<Object>> properties) {
        this.properties = properties;
    }

    public String getNodeX() {
        return nodeX;
    }

    public void setNodeX(String nodeX) {
        this.nodeX = nodeX;
    }

    public String getNodeY() {
        return nodeY;
    }

    public void setNodeY(String nodeY) {
        this.nodeY = nodeY;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
