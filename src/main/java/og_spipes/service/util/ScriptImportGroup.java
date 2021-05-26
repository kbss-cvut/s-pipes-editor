package og_spipes.service.util;

import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.service.ScriptService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
            List<Statement> res = defaultModel
                    .read(ff.getAbsolutePath(), org.apache.jena.util.FileUtils.langTurtle)
                    .listStatements(null, RDF.type, OWL.Ontology).toList();
            if(res.size() > 0){
                String importName = res.get(0).getSubject().getURI();
                scriptImportNameFile.put(importName, ff);
            }
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
        Model defaultModel = ModelFactory.createDefaultModel();
        defaultModel.removeAll();
        return defaultModel
                .read(file.getAbsolutePath(), FileUtils.langTurtle)
                .listStatements(null, OWL.imports, (RDFNode) null)
                .toList()
                .stream().map(x -> x.getObject().toString())
                .filter(x -> !x.equals("http://onto.fel.cvut.cz/ontologies/s-pipes-lib"))
                .collect(Collectors.toList());
    }

    public Set<File> getUsedFiles() {
        return usedFiles;
    }
}
