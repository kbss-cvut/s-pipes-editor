package og_spipes.service;

import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.TransformationDTO;
import og_spipes.model.view.Edge;
import og_spipes.model.view.Node;
import og_spipes.model.view.View;
import og_spipes.persistence.dao.TransformationDAO;
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

    @Autowired
    public ViewService(ScriptService scriptService, TransformationDAO transformationDAO) {
        this.scriptService = scriptService;
        this.transformationDAO = transformationDAO;
    }

    public View newViewFromSpipes(String script, String transformationId) {
        LOG.info("Creating a view for script " + script);
        List<Module> modules = scriptService.getModules(script);
        Map<URI, NodeExecutionInfo> inOutParams = resolveInOutParams(transformationId);
        List<Node> nodes = modules.stream().map(m -> {
            URI mUri = m.getUri();
            Set<String> in = inOutParams.containsKey(mUri) ? inOutParams.get(mUri).getInput() : new HashSet<>();
            Set<String> out = inOutParams.containsKey(mUri) ? inOutParams.get(mUri).getOutput() : new HashSet<>();
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
                            out
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

    //TODO refactor later
    public Map<URI, NodeExecutionInfo> resolveInOutParams(String  transformationId){
        TransformationDTO transformation;
        try {
            transformation = transformationDAO.find(new URI(transformationId));
        } catch (URISyntaxException | NullPointerException e) {
            LOG.warn("Transformation uri is not valid: {}", e.getMessage());
            return new HashMap<>();
        }
        Set<URI> uris = transformation.getProperties().get("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/has-part")
                .stream().map(x -> URI.create(x.toString())).collect(Collectors.toSet());

        List<TransformationDTO> transformationDTOS = transformationDAO.find(uris);
        LOG.info("Execution has following transformations: {}", uris);

        //Node_URI -> Set<NodeExecutionInfo>
        Map<URI, NodeExecutionInfo> nodesExecution = new HashMap<>();
        for(TransformationDTO t : transformationDTOS){
            URI moduleId = URI.create(t.getProperties().get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-id").stream().findFirst().orElse("").toString());
            NodeExecutionInfo executionInfo = new NodeExecutionInfo();
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

        return nodesExecution;
    }


}
