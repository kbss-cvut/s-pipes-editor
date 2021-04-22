package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.kbss.jopa.vocabulary.DC;
import cz.cvut.kbss.jopa.vocabulary.RDFS;
import cz.cvut.spipes.Vocabulary;
import cz.cvut.spipes.model.ExecutionContextDatasetSource;
import cz.cvut.spipes.model.SourceDatasetSnapshot;
import cz.cvut.spipes.model.Thing;
import cz.cvut.spipes.model.Transformation;

import java.net.URI;
import java.util.Map;
import java.util.Set;

@OWLClass(
        iri = "http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation"
)
public class TransformationDTO extends Transformation {

    public TransformationDTO() {
    }

    public TransformationDTO(
            String id,
            Map<String, Set<Object>> properties
    ) {
        this.id = id;
        this.properties = properties;
    }


}
