package og_spipes.service;

import og_spipes.persistence.dao.OntologyDao;
import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.service.util.ScriptImportGroup;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptOntologyHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptOntologyHelper.class);
    private final String[] scriptPath;

    public ScriptOntologyHelper(String[] scriptPath) {
        this.scriptPath = scriptPath;
    }

    /**
     * basic implementation without optimalization
     */
    public Map<URI, File> moduleFile(Set<URI> modules){
        Map<URI, File> res = new HashMap<>();

        List<File> files = ScriptDAO.getScripts(scriptPath);
        for(File ff : files){

            String baseURI = OntologyDao.getOntologyUri(ff);
            if(!baseURI.isBlank()){
                String importName = baseURI + "/";
                List<String> okModules = modules.stream().map(URI::toString)
                        .filter(x -> !x.replace(importName, "").contains("/"))
                        .collect(Collectors.toList());

                Set<String> subjects = OntologyDao.getSubjects(ff);
                for(String module : okModules){
                    if(subjects.stream().anyMatch(x -> x.equals(module))){
                        res.put(URI.create(module), ff);
                    }
                }
            }
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
