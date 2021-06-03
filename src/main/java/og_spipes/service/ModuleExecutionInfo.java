package og_spipes.service;

import cz.cvut.kbss.jopa.model.annotations.*;
import og_spipes.model.Vocabulary;
import og_spipes.model.dto.ExecutionVariableDTO;

import java.util.HashSet;
import java.util.Set;

import static og_spipes.model.Vocabulary.*;

@MappedSuperclass
@OWLClass(iri = Vocabulary.s_c_module_execution_info)
public class ModuleExecutionInfo {

    @OWLDataProperty(iri = s_p_has_module_uri)
    private String moduleUri;

    @OWLObjectProperty(iri = s_p_has_module_variable,  fetch = FetchType.EAGER)
    private Set<ExecutionVariableDTO> moduleVariables = new HashSet<>();

    @OWLDataProperty(iri = s_p_module_execution_duration)
    private Long executionDuration;

    @OWLDataProperty(iri = s_p_module_execution_start_date_unix)
    private Long executionStartDate;

    @OWLDataProperty(iri = s_p_module_execution_finish_date_unix)
    private Long executionFinishDate;

    @OWLObjectProperty(iri = s_pipes_module_has_input_path, fetch = FetchType.EAGER)
    private Set<String> input = new HashSet<>();

    @OWLObjectProperty(iri = s_pipes_module_has_output_path, fetch = FetchType.EAGER)
    private Set<String> output = new HashSet<>();

    public ModuleExecutionInfo() {
    }

    public ModuleExecutionInfo(String moduleUri, Set<ExecutionVariableDTO> moduleVariables, Long executionDuration, Long executionStartDate, Long executionFinishDate) {
        this.moduleUri = moduleUri;
        this.moduleVariables = moduleVariables;
        this.executionDuration = executionDuration;
        this.executionStartDate = executionStartDate;
        this.executionFinishDate = executionFinishDate;
    }

    public ModuleExecutionInfo(String moduleUri, Set<ExecutionVariableDTO> moduleVariables, Long executionDuration, Long executionStartDate, Long executionFinishDate, Set<String> input, Set<String> output) {
        this.moduleUri = moduleUri;
        this.moduleVariables = moduleVariables;
        this.executionDuration = executionDuration;
        this.executionStartDate = executionStartDate;
        this.executionFinishDate = executionFinishDate;
        this.input = input;
        this.output = output;
    }

    public String getModuleUri() {
        return moduleUri;
    }

    public void setModuleUri(String moduleUri) {
        this.moduleUri = moduleUri;
    }

    public Set<ExecutionVariableDTO> getModuleVariables() {
        return moduleVariables;
    }

    public void setModuleVariables(Set<ExecutionVariableDTO> moduleVariables) {
        this.moduleVariables = moduleVariables;
    }

    public Long getExecutionDuration() {
        return executionDuration;
    }

    public void setExecutionDuration(String executionDuration) {
        this.executionDuration = Long.parseLong(executionDuration);
    }

    public void setExecutionDuration(Long executionDuration) {
        this.executionDuration = executionDuration;
    }

    public Long getExecutionStartDate() {
        return executionStartDate;
    }

    public void setExecutionStartDate(String executionStartDate) {
        this.executionStartDate = Long.parseLong(executionStartDate);
    }

    public void setExecutionStartDate(Long executionStartDate) {
        this.executionStartDate = executionStartDate;
    }

    public Long getExecutionFinishDate() {
        return executionFinishDate;
    }

    public void setExecutionFinishDate(String executionFinishDate) {
        this.executionFinishDate = Long.parseLong(executionFinishDate);
    }

    public void setExecutionFinishDate(Long executionFinishDate) {
        this.executionFinishDate = executionFinishDate;
    }

    public Set<String> getInput() {
        return input;
    }

    public void setInput(Set<String> input) {
        this.input = input;
    }

    public Set<String> getOutput() {
        return output;
    }

    public void setOutput(Set<String> output) {
        this.output = output;
    }

    public void merge(ModuleExecutionInfo executionInfo) {
        input.addAll(executionInfo.getInput());
        output.addAll(executionInfo.getOutput());
    }

    @Override
    public String toString() {
        return "ModuleExecutionInfo{" +
                "moduleUri='" + moduleUri + '\'' +
                ", moduleVariables=" + moduleVariables +
                ", executionDuration=" + executionDuration +
                ", executionStartDate=" + executionStartDate +
                ", executionFinishDate=" + executionFinishDate +
                ", input=" + input +
                ", output=" + output +
                '}';
    }
}
