package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;

import java.net.URI;

import static og_spipes.model.Vocabulary.s_c_Modules;
import static og_spipes.model.Vocabulary.s_p_label;

@OWLClass(iri = s_c_Modules)
public class TestJSONLD {

    @Id(generated = true)
    protected URI uri;

    @OWLDataProperty(iri = s_p_label)
    private String label;

    public TestJSONLD() {
    }

    public TestJSONLD(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public URI getUri() {
        return uri;
    }
}
