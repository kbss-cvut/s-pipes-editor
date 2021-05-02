package og_spipes.model.spipes;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import og_spipes.model.AbstractEntitySP;
import org.eclipse.rdf4j.query.algebra.Str;

import java.util.Date;
import java.util.Set;

import static og_spipes.model.Vocabulary.*;

@OWLClass(iri = s_c_execution_dto)
public class ExecutionDTO extends AbstractEntitySP {

    @OWLDataProperty(iri = s_p_has_name)
    private String name;

    @OWLDataProperty(iri = s_p_has_display_name)
    private String displayName;

    @OWLDataProperty(iri = s_p_has_absolute_path)
    private String absolutePath;

    //TODO - how to get this one from s-pipes Vocabulary
    @OWLDataProperty(iri = "http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-duration")
    private Long duration;

    @OWLDataProperty(iri = "http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-start-date-unix")
    private Date startDate;

    @OWLDataProperty(iri = "http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-finish-date-unix")
    private Date finishDate;

    @OWLDataProperty(iri = "http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation")
    private String transformationId;

    public ExecutionDTO(){}

    public ExecutionDTO(String name, String displayName, String absolutePath, Long duration, Date startDate, Date finishDate, String transformation) {
        this.name = name;
        this.displayName = displayName;
        this.absolutePath = absolutePath;
        this.duration = duration;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.transformationId = transformation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public String getTransformation() {
        return transformationId;
    }

    public void setTransformation(String transformation) {
        this.transformationId = transformation;
    }

    @Override
    public String toString() {
        return "ExecutionDTO{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                ", duration=" + duration +
                ", startDate=" + startDate +
                ", finishDate=" + finishDate +
                ", transformation=" + transformationId +
                '}';
    }
}

