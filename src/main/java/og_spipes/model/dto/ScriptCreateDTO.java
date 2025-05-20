package og_spipes.model.dto;

import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.sforms.model.AbstractEntity;
import og_spipes.model.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.Sequence;

import java.util.List;

import static og_spipes.model.Vocabulary.*;

@MappedSuperclass
@OWLClass(iri = Vocabulary.s_c_script_create_dto)
public class ScriptCreateDTO extends AbstractEntity {

    @OWLDataProperty(iri = s_p_has_script_path)
    private String directoryPath;

    @OWLDataProperty(iri = s_p_has_name)
    private String name;

    @OWLDataProperty(iri = s_p_has_ontology_uri)
    private String ontologyUri;

    @OWLDataProperty(iri = s_p_has_return_module_name)
    private String returnModuleName;

    @OWLDataProperty(iri = s_p_has_function_name)
    private String functionName;

    @OWLDataProperty(iri = s_p_has_function_arguments)
    @Sequence
    private List<ScriptFunctionArgument> scriptFunctionArguments;

    public ScriptCreateDTO() {
    }
    public ScriptCreateDTO(String directoryPath, String name, String ontologyUri, String returnModuleName, String functionName, List<ScriptFunctionArgument> scriptFunctionArguments) {
        this.directoryPath = directoryPath;
        this.name = name;
        this.ontologyUri = ontologyUri;
        this.returnModuleName = returnModuleName;
        this.functionName = functionName;
        this.scriptFunctionArguments = scriptFunctionArguments;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOntologyUri() {
        return ontologyUri;
    }

    public void setOntologyUri(String ontologyUri) {
        this.ontologyUri = ontologyUri;
    }

    public String getReturnModuleName() {
        return returnModuleName;
    }

    public void setReturnModuleName(String returnModuleName) {
        this.returnModuleName = returnModuleName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public List<ScriptFunctionArgument> getFunctionArguments() {
        return scriptFunctionArguments;
    }

    public void setFunctionArguments(List<ScriptFunctionArgument> scriptFunctionArguments) {
        this.scriptFunctionArguments = scriptFunctionArguments;
    }
    @Override
    public String toString() {
        return "ScriptCreateDTO{" +
                "directoryPath='" + directoryPath + '\'' +
                ", name='" + name + '\'' +
                ", ontologyUri='" + ontologyUri + '\'' +
                ", returnModuleName='" + returnModuleName + '\'' +
                ", functionName='" + functionName + '\'' +
                ", functionArguments=" + scriptFunctionArguments +
                '}';
    }


}

