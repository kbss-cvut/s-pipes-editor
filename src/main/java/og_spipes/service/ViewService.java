package og_spipes.service;

import cz.cvut.spipes.model.Thing;
import cz.cvut.spipes.model.Transformation;
import og_spipes.model.dto.ExecutionVariableDTO;
import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.TransformationDTO;
import og_spipes.model.view.Edge;
import og_spipes.model.view.Node;
import og_spipes.model.view.View;
import og_spipes.persistence.dao.TransformationDAO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ViewService {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptService.class);
    private final ScriptService scriptService;
    private final TransformationDAO transformationDAO;
    private final RepositoryContextJsonLoader repositoryContextJsonLoader;

    @Autowired
    public ViewService(ScriptService scriptService, TransformationDAO transformationDAO, RepositoryContextJsonLoader repositoryContextJsonLoader) {
        this.scriptService = scriptService;
        this.transformationDAO = transformationDAO;
        this.repositoryContextJsonLoader = repositoryContextJsonLoader;
    }

    public View newViewFromSpipes(String script, String transformationId) {
        LOG.info("Creating a view for script " + script);
        List<Module> modules = scriptService.getModules(script);
        Map<URI, ModuleExecutionInfo> modulesExecutioInfo = resolveModuleExecutionInfo(transformationId);
        List<Node> nodes = modules.stream().map(m -> {
            URI mUri = m.getUri();
            Set<String> in = modulesExecutioInfo.containsKey(mUri) ? modulesExecutioInfo.get(mUri).getInput() : new HashSet<>();
            Set<String> out = modulesExecutioInfo.containsKey(mUri) ? modulesExecutioInfo.get(mUri).getOutput() : new HashSet<>();
            Set<ExecutionVariableDTO> moduleVariables = modulesExecutioInfo.containsKey(mUri) ? modulesExecutioInfo.get(mUri).getModuleVariables() : new HashSet<>();
            return new Node(
                            m.getUri(),
                            m.getId(),
                            m.getLabel(),
                            m.getSpecificType() == null ? null : m.getSpecificType().getUri().toString(),
                            m.getNodeX(),
                            m.getNodeY(),
                            m.getScriptPath(),
                            m.getSource(),
                            m.getTypes(),
                            in,
                            out,
                            moduleVariables
                    );
        }).collect(Collectors.toList());
        List<Edge> edges = new ArrayList<>();
        for (Module m : modules) {
            if (m.getNext() != null) {
                for (Module n : m.getNext()) {
                    if (n != null) {
                        Edge edge = new Edge(
                                nodes.stream().filter(a -> a.getUri() == m.getUri()).findAny().get(),
                                nodes.stream().filter(a -> a.getUri() == n.getUri()).findAny().get()
                        );
                        edges.add(edge);
                    }
                }
            }
        }
        return new View(script, new HashSet<>(nodes), new HashSet<>(edges));
    }

    public Map<URI, ModuleExecutionInfo> resolveModuleExecutionInfo(String transformationId){
        Set<ModuleExecutionInfo> moduleExecutionInfos = modulesExecutionInfo(transformationId);
        HashMap<URI, ModuleExecutionInfo> executionInfo = new HashMap<>();
        moduleExecutionInfos.forEach(x -> {
            executionInfo.put(URI.create(x.getModuleUri()), x);
        });
        return executionInfo;
    }

    public Set<ModuleExecutionInfo> modulesExecutionInfo(String transformationId){
        TransformationDTO transformation;
        try {
            transformation = transformationDAO.find(new URI(transformationId));
            if(transformation == null){
                LOG.warn("Transformation is not found: {}", transformationId);
                return new HashSet<>();
            }
        } catch (URISyntaxException | NullPointerException e) {
            LOG.warn("Transformation uri is not valid: {}", e.getMessage());
            return new HashSet<>();
        }

//        Set<URI> uris = transformation.getHas_part().stream()
//            .map(t -> URI.create(t.getId())).collect(Collectors.toSet());
//
//        List<TransformationDTO> transformationDTOS = transformationDAO.find(uris);
        Set<Transformation> transformationParts = transformation.getHas_part();
        LOG.info("Execution has following transformations: {}",
            transformationParts.stream().map(Transformation::getId).collect(Collectors.toList()) );

        //Node_URI -> Set<NodeExecutionInfo>
        Map<URI, ModuleExecutionInfo> nodesExecution = new HashMap<>();
        for(Transformation t : transformationParts){

            //Use later in mapping
            URI inputBinding = URI.create(
                t.getHas_input_binding().stream().map(Thing::getId).findFirst().orElse("")
            );
            Set<ExecutionVariableDTO> variableDTOSet = loadExecutionVariables(inputBinding);

            String moduleUri = t.getHas_module_id();
            URI moduleId = URI.create(moduleUri);
            ModuleExecutionInfo executionInfo = new ModuleExecutionInfo();
            executionInfo.setModuleVariables(variableDTOSet);
            executionInfo.setModuleUri(moduleUri);
            executionInfo.setExecutionDuration(
                t.getProperties().get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-execution-duration")
                .iterator().next().toString()
            );
            executionInfo.setExecutionStartDate(
                (Long) t.getProperties().get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-execution-start-date-unix")
                    .iterator().next()
            );
            executionInfo.setExecutionFinishDate(
                (Long) t.getProperties().get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-execution-finish-date-unix")
                    .iterator().next()
            );
            File file = new File(t.getHas_input().getId().replace("file:", ""));
            executionInfo.getInput().add(file.getAbsolutePath());
            List<File> outputs = t.getHas_output().stream().map(x -> new File(x.getId().replace("file:", ""))).collect(Collectors.toList());
            for (File f : outputs){
                executionInfo.getOutput().add(f.getAbsolutePath());
            }

            if(nodesExecution.containsKey(moduleId)){
                nodesExecution.get(moduleId).merge(executionInfo);
            }else{
                nodesExecution.put(moduleId, executionInfo);
            }
            LOG.info("Execution info: {}", executionInfo);
        }

        return new HashSet<>(nodesExecution.values());
    }

    private Set<ExecutionVariableDTO> loadExecutionVariables(URI contextUri) {
        Set<ExecutionVariableDTO> variableDTOSet = new HashSet<>();
        String jsonContext = repositoryContextJsonLoader.contextAsJson(contextUri);
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonContext);
        } catch (JSONException e) {
            LOG.warn("Context can not be loaded {}", e.getMessage());
        }
        for (Iterator it = Objects.requireNonNull(obj).keys(); it.hasNext(); ) {
            String s = it.next().toString();

            try {
                JSONArray jsonArray = obj.getJSONObject(s).getJSONArray("http://onto.fel.cvut.cz/ontologies/s-pipes/has_bound_value");
                JSONObject obj2 = new JSONObject(jsonArray.get(0).toString());
                String value = obj2.getString("value");

                JSONArray jsonArray2 = obj.getJSONObject(s).getJSONArray("http://onto.fel.cvut.cz/ontologies/s-pipes/has_bound_variable");
                JSONObject obj3 = new JSONObject(jsonArray2.get(0).toString());
                String varName = obj3.getString("value");

                ExecutionVariableDTO executionVariableDTO = new ExecutionVariableDTO(varName, value);
                variableDTOSet.add(executionVariableDTO);
            }catch (JSONException ignored){}
        }

        return variableDTOSet;
    }

}
