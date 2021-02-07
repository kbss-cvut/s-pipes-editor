package og_spipes.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;

import java.net.URI;

@MappedSuperclass
public abstract class AbstractEntitySP {

    @Id(generated = true)
    protected URI uri;
    @OWLDataProperty(iri = Vocabulary.s_p_identifier)
    protected String id;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}