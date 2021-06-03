package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.spipes.model.Transformation;

import java.util.Map;
import java.util.Set;

@OWLClass(
        iri = "http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation"
)
public class TransformationDTO extends Transformation {

//    //TODO ask why this attribute is not possible to map??? Also I lose properties field
//    @OWLDataProperty(iri = "http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-name")
//    private String pipelineName;
//
//    @OWLDataProperty(iri = "http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-duration")
//    private Long executionDuration;
//
//    @OWLDataProperty(iri = "http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-start-date")
//    private Date executionStartDate;
//
//    @OWLDataProperty(iri = "http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-finish-date")
//    private Date executionFinishDate;

    public TransformationDTO() {
    }

    public TransformationDTO(
            String id,
            Map<String, Set<Object>> properties
    ) {
        this.id = id;
        this.properties = properties;
    }

//    public String getPipelineName() {
//        return pipelineName;
//    }
//
//    public void setPipelineName(String pipelineName) {
//        this.pipelineName = pipelineName;
//    }
//
//    public Long getExecutionDuration() {
//        return executionDuration;
//    }
//
//    public void setExecutionDuration(Long executionDuration) {
//        this.executionDuration = executionDuration;
//    }
//
//    public Date getExecutionStartDate() {
//        return executionStartDate;
//    }
//
//    public void setExecutionStartDate(Date executionStartDate) {
//        this.executionStartDate = executionStartDate;
//    }
//
//    public Date getExecutionFinishDate() {
//        return executionFinishDate;
//    }
//
//    public void setExecutionFinishDate(Date executionFinishDate) {
//        this.executionFinishDate = executionFinishDate;
//    }

    public String getProperty(String property){
        return properties.get(property).stream().findFirst().orElse("").toString();
    }

}
