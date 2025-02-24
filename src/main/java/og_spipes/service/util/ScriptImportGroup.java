package og_spipes.service.util;

import og_spipes.persistence.dao.OntologyDao;
import og_spipes.persistence.dao.ScriptDAO;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class ScriptImportGroup {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptImportGroup.class);

    private final Map<String, File> scriptImportNameFile;
    private final Set<File> usedFiles;

    private final String[] scriptPath;

    public ScriptImportGroup(String[] scriptPath, File script) {
        this.scriptImportNameFile = new HashMap<>();
        this.usedFiles = new HashSet<>();
        this.scriptPath = scriptPath;

        find(script);
    }

    private void find(File script) {
        List<File> files = ScriptDAO.getScripts(scriptPath);
        Model defaultModel = ModelFactory.createDefaultModel();
        for(File ff : files){
            defaultModel.removeAll();
            String importName = OntologyDao.getOntologyUri(ff);
            if(importName != null && !importName.isBlank())
                scriptImportNameFile.put(importName, ff);
        }

        findFiles(script);
        usedFiles.add(script);
    }

    private void findFiles(File f){
        List<String> imports = scriptImports(f);
        for(String s : imports){
            File file = scriptImportNameFile.get(s);
            if(file == null){
                LOG.warn("Import: " + s + ", do not have corresponding file.");
            }
            if(file != null && !usedFiles.contains(file)){
                usedFiles.add(file);
                findFiles(file);
            }
        }
    }

    private List<String> scriptImports(File file){
        return OntologyDao.getOntologyImports(file);
    }

    public Set<File> getUsedFiles() {
        return usedFiles;
    }
}
