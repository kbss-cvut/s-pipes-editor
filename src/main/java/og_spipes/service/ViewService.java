package og_spipes.service;

import og_spipes.model.spipes.Module;
import og_spipes.model.view.Edge;
import og_spipes.model.view.Node;
import og_spipes.model.view.View;
import og_spipes.persistence.dao.ViewDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ViewService {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptService.class);
    private final ScriptService scriptService;

    @Autowired
    public ViewService(ScriptService scriptService) {
        this.scriptService = scriptService;
    }

    public View newViewFromSpipes(String script) {
        LOG.info("Creating a view for script " + script);
        List<Module> modules = scriptService.getModules(script);
        List<Node> nodes = modules.stream().map(m -> new Node(
                m.getUri(),
                m.getId(),
                m.getLabel(),
                m.getSpecificType().getUri().toString(),
                0.0,
                0.0,
                m.getTypes(),
                new HashSet<>(),
                new HashSet<>()
        )
        ).collect(Collectors.toList());
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
        View view = new View(script, new HashSet<>(nodes), new HashSet<>(edges));

        return view;
    }

}
