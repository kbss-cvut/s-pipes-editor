package og_spipes.model.view;

import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import og_spipes.model.AbstractEntitySP;
import og_spipes.model.dto.ExecutionVariableDTO;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static og_spipes.model.Vocabulary.*;

@OWLClass(iri = s_c_node)
public class Node extends AbstractEntitySP {

    @OWLDataProperty(iri = s_p_label)
    private String label;
    @OWLDataProperty(iri = s_p_component)
    private String component;
    @OWLDataProperty(iri = s_p_has_x_coordinate)
    private Integer x;
    @OWLDataProperty(iri = s_p_has_y_coordinate)
    private Integer y;
    @OWLDataProperty(iri = s_p_has_script_path)
    private String scriptPath;
    @OWLDataProperty(iri = s_p_group)
    private String group;
    @OWLDataProperty(iri = s_p_has_module_type)
    private Set<String> moduleTypes;
    @OWLDataProperty(iri = s_p_has_input_parameter)
    private Set<String> inParameters;
    @OWLDataProperty(iri = s_p_has_output_parameter)
    private Set<String> outParameters;
    @OWLObjectProperty(iri = s_p_has_module_variable,  fetch = FetchType.EAGER)
    private Set<ExecutionVariableDTO> moduleVariables = new HashSet<>();

    public Node() {
    }

    public Node(URI uri, String id, String label, String component, String x, String y, String scriptPath, String group, Set<String> moduleTypes, Set<String> inParameters, Set<String> outParameters, Set<ExecutionVariableDTO> moduleVariables) {
        this.uri = uri;
        this.id = id;
        this.label = label;
        this.component = component;
        this.x = x != null ? Integer.parseInt(x) : null;
        this.y = y != null ? Integer.parseInt(y) : null;
        this.scriptPath = scriptPath;
        this.group = group;
        this.moduleTypes = moduleTypes;
        this.inParameters = inParameters;
        this.outParameters = outParameters;
        this.moduleVariables = moduleVariables;
    }

    public Node(URI uri, String id, String label, String component, Integer x, Integer y, String scriptPath, String group, Set<String> moduleTypes, Set<String> inParameters, Set<String> outParameters, Set<ExecutionVariableDTO> moduleVariables) {
        this.uri = uri;
        this.id = id;
        this.label = label;
        this.component = component;
        this.x = x;
        this.y = y;
        this.scriptPath = scriptPath;
        this.group = group;
        this.moduleTypes = moduleTypes;
        this.inParameters = inParameters;
        this.outParameters = outParameters;
        this.moduleVariables = moduleVariables;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Set<String> getModuleTypes() {
        return moduleTypes;
    }

    public void setModuleTypes(Set<String> moduleTypes) {
        this.moduleTypes = moduleTypes;
    }

    public Set<String> getInParameters() {
        return inParameters;
    }

    public void setInParameters(Set<String> inParameters) {
        this.inParameters = inParameters;
    }

    public Set<String> getOutParameters() {
        return outParameters;
    }

    public void setOutParameters(Set<String> outParameters) {
        this.outParameters = outParameters;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public Set<ExecutionVariableDTO> getModuleVariables() {
        return moduleVariables;
    }

    public void setModuleVariables(Set<ExecutionVariableDTO> moduleVariables) {
        this.moduleVariables = moduleVariables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(getUri(), node.getUri()) && Objects.equals(getLabel(), node.getLabel()) && Objects.equals(getComponent(), node.getComponent()) && Objects.equals(getX(), node.getX()) && Objects.equals(getY(), node.getY()) && Objects.equals(getScriptPath(), node.getScriptPath()) && Objects.equals(getGroup(), node.getGroup()) && Objects.equals(getModuleTypes(), node.getModuleTypes()) && Objects.equals(getInParameters(), node.getInParameters()) && Objects.equals(getOutParameters(), node.getOutParameters()) && Objects.equals(moduleVariables, node.moduleVariables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri(), getLabel(), getComponent(), getX(), getY(), getScriptPath(), getGroup(), getModuleTypes(), getInParameters(), getOutParameters(), moduleVariables);
    }
}
