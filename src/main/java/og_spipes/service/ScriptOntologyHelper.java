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
     * Maps SPipes module URIs to their corresponding ontology files using prefix-based matching and checking
     * that the module URI is found as a subject within that file.
     * <p>
     * A module is mapped to a file if its URI follows the pattern {@code <ontologyURI>/<moduleName>},
     * where {@code <ontologyURI>} is declared as {@code a owl:Ontology} in the file,
     * and the module URI is found as a subject within that file.
     * </p>
     *
     * @param modules the set of module URIs to resolve
     * @return a map where keys are module URIs and values are the files containing those modules
     */
    public Map<URI, File> getModule2FileMappingBasedOnPrefix(Set<URI> modules){
        Map<URI, File> res = new HashMap<>();

        List<File> files = ScriptDAO.getScripts(scriptPath);
        for(File ff : files){

            String baseURI = OntologyDao.getOntologyUri(ff);
            if(!baseURI.isBlank()){
                List<URI> okModules = modules.stream()
                        .filter(x -> x.toString().matches(baseURI + "[/#]" + "[^/#]+"))
                        .toList();

                Set<String> subjects = OntologyDao.getSubjects(ff);
                for(URI module : okModules){
                    if(subjects.stream().anyMatch(m -> m.equals(module.toString()))){
                        res.put(module, ff);
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
