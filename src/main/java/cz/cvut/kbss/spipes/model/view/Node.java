package cz.cvut.kbss.spipes.model.view;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.spipes.model.AbstractEntity;

import java.net.URI;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static cz.cvut.kbss.spipes.model.Vocabulary.*;

/**
 * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 10.11.16.
 */
@OWLClass(iri = s_c_node)
public class Node extends AbstractEntity {

    @OWLDataProperty(iri = s_p_label)
    private String label;
    @OWLDataProperty(iri = s_p_has_x_coordinate)
    private Double x = 0.0;
    @OWLDataProperty(iri = s_p_has_y_coordinate)
    private Double y = 0.0;

    /**
     * Types that correspond to the-graph library types and specify icon & stuff
     */
    @OWLDataProperty(iri = s_p_has_module_type)
    private Set<String> moduleTypes;
    @OWLDataProperty(iri = s_p_has_input_parameter)
    private Set<String> inParameters;
    @OWLDataProperty(iri = s_p_has_output_parameter)
    private Set<String> outParameters;

    public Node() {
    }

    public Node(String label, double x, double y, Set<String> moduleTypes, Set<String> inParameters, Set<String> outParameters) {
        this.id = UUID.randomUUID().toString();
        this.uri = URI.create(s_c_node + "/" + id);
        this.label = label;
        this.x = x;
        this.y = y;
        this.moduleTypes = moduleTypes;
        this.inParameters = inParameters;
        this.outParameters = outParameters;
    }

    public Node(URI uri, String id, String label, Double x, Double y, Set<String> moduleTypes, Set<String> inParameters, Set<String> outParameters) {
        this.uri = uri;
        this.id = id;
        this.label = label;
        this.x = x;
        this.y = y;
        this.moduleTypes = moduleTypes;
        this.inParameters = inParameters;
        this.outParameters = outParameters;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(getLabel(), node.getLabel()) &&
                Objects.equals(getX(), node.getX()) &&
                Objects.equals(getY(), node.getY()) &&
                Objects.equals(getModuleTypes(), node.getModuleTypes()) &&
                Objects.equals(getInParameters(), node.getInParameters()) &&
                Objects.equals(getOutParameters(), node.getOutParameters());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getLabel(), getX(), getY(), getModuleTypes(), getInParameters(), getOutParameters());
    }
}
