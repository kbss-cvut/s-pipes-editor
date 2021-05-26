package og_spipes.service;

import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.service.util.ScriptImportGroup;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptGroupsHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptGroupsHelper.class);
    private final String[] scriptPath;

    public ScriptGroupsHelper(String[] scriptPath) {
        this.scriptPath = scriptPath;
    }

    /**
     * basic implementation without optimalization
     */
    public Map<URI, File> moduleFile(Set<URI> modules){
        Map<URI, File> res = new HashMap<>();
        Model defaultModel = ModelFactory.createDefaultModel();

        List<File> files = ScriptDAO.getScripts(scriptPath);
        for(File ff : files){
            Model model = defaultModel.read(ff.getAbsolutePath(), FileUtils.langTurtle);

            List<Statement> baseURI = model.listStatements(null, RDF.type, OWL.Ontology).toList();
            if(baseURI.size() > 0){
                String importName = baseURI.get(0).getSubject().getURI() + "/";
                List<String> okModules = modules.stream().map(URI::toString)
                        .filter(x -> !x.replace(importName, "").contains("/"))
                        .collect(Collectors.toList());

                List<Resource> subjects = model.listSubjects().toList();
                for(String module : okModules){
                    if(subjects.stream().anyMatch(x -> x.getURI() != null && x.getURI().equals(module))){
                        res.put(URI.create(module), ff);
                    }
                }
            }
            model.removeAll();
        }

        return res;
    }

    public Map<URI, Set<String>> resolveFileGroups(File file, Set<URI> modules) {
        Model defaultModel = ModelFactory.createDefaultModel();
        ScriptImportGroup importGroup = new ScriptImportGroup(scriptPath, file);

        //VERY COSTLY! - consider better approach
        Map<URI, Set<String>> res = new HashMap<>();
        for(URI m : modules){
            res.put(m, new HashSet<>());
            for (File f : importGroup.getUsedFiles()){
                defaultModel.removeAll();
                ResourceFactory.createResource(m.toString());
                List<Statement> contains = defaultModel
                        .read(f.getAbsolutePath(), org.apache.jena.util.FileUtils.langTurtle)
                        .listStatements(ResourceFactory.createResource(m.toString()), null, (RDFNode) null).toList();
                if(contains.size() > 0){
                    res.get(m).add(f.getName());
                }
            }
        }

        return res;
    }

}
