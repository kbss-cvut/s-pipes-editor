package og_spipes.service;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import og_spipes.model.Vocabulary;
import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.service.exception.FileExistsException;
import og_spipes.service.exception.MissingOntologyException;
import og_spipes.service.exception.OntologyDuplicationException;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScriptService {

    private static final Logger LOG = LoggerFactory.getLogger(OntologyHelper.class);

    private final ScriptDAO scriptDao;
    private final OntologyHelper ontologyHelper;

    @Autowired
    public ScriptService(ScriptDAO scriptDao, OntologyHelper ontologyHelper) {
        this.scriptDao = scriptDao;
        this.ontologyHelper = ontologyHelper;
    }

    public List<Module> getModules(String filepath){
        Model ontModel = ontologyHelper.createOntModel(new File(filepath));
        return scriptDao.getModules(ontModel);
    }

    public List<ModuleType> getModuleTypes(String filepath){
        Model ontModel = ontologyHelper.createOntModel(new File(filepath));
        return scriptDao.getModuleTypes(ontModel);
    }

    //TODO test later - quite hard now
    public void createDependency(String scriptPath, String from, String to) throws FileNotFoundException {
        Model ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        List<Resource> resources = ontModel.listSubjects().toList().stream().filter(Objects::nonNull).filter(x -> x.getURI() != null).collect(Collectors.toList());
        Optional<Resource> moduleFrom = resources.stream().filter(x -> x.getURI().equals(from)).findAny();
        Optional<Resource> moduleTo = resources.stream().filter(x -> x.getURI().equals(to)).findAny();

        if(!moduleFrom.isPresent() || !moduleTo.isPresent()){
            throw new IllegalArgumentException("FROM MODULE: " + moduleFrom + " OR TO MODULE " + moduleTo + "CANT BE NULL");
        }

        ontModel.add(moduleFrom.get(), new PropertyImpl(Vocabulary.s_p_next), moduleTo.get());
        FileOutputStream os = new FileOutputStream(scriptPath);
        ontModel.write(os, FileUtils.langTurtle);

        //TODO notification webhooks
    }

    //TODO test later - quite hard now
    public void deleteDependency(String scriptPath, String from, String to) throws FileNotFoundException {
        Model ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        ontModel.removeAll(
                ontModel.getResource(from),
                new PropertyImpl(Vocabulary.s_p_next),
                ontModel.getResource(to)
        );
        FileOutputStream os = new FileOutputStream(scriptPath);
        ontModel.write(os, FileUtils.langTurtle);

        //TODO notification webhooks
    }

    //TODO test later - quite hard now
    public void deleteModule(String scriptPath, String module) throws FileNotFoundException {
        Model ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        ontModel.removeAll(ontModel.getResource(module), null, null);
        ontModel.removeAll(null, null, ontModel.getResource(module));
        FileOutputStream os = new FileOutputStream(scriptPath);
        ontModel.write(os, FileUtils.langTurtle);

        //TODO notification webhooks
    }

    /**
     * Basic concept is done, but constrains are necessary. Also clarify correctness of the solution.
     * Constrains suggestions:
     * Module is used by another script which imports the previous script
     * Module name already exist in scriptTo file
     * Next property is problematic - actual solution still point out on URI of the previous module. However common approach is to use imports, so it should consistent.
     * Some others?
     * @throws FileNotFoundException
     */
    public void moveModule(String scriptFrom, String scriptTo, String moduleURI) throws FileNotFoundException {
        //TODO add constrains - very problematic, could cause issues!

        Model fromModel = ontologyHelper.createOntModel(new File(scriptFrom));
        List<Statement> statements = fromModel.listStatements(fromModel.getResource(moduleURI), null, (RDFNode) null).toList();
        List<Statement> statements1 = fromModel.listStatements(null, null, fromModel.getResource(moduleURI)).toList();
        List<Statement> fromStatements = Stream.concat(statements.stream(), statements1.stream())
                .collect(Collectors.toList());

        if(fromStatements.size() == 0){
            LOG.error("Module not found! " + moduleURI);
        }

        Model toModel = ontologyHelper.createOntModel(new File(scriptTo));
        toModel.add(fromStatements);
        FileOutputStream os = new FileOutputStream(scriptTo);
        toModel.write(os, FileUtils.langTurtle);

        deleteModule(scriptFrom, moduleURI);
    }

    public void createScript(String directory, String filename, URI ontologyURI) throws IOException, OntologyDuplicationException, FileExistsException {
        File template = new File("src/main/resources/template/hello-world3.sms.ttl");

        List<String> ontologyNames = scriptDao.getScripts().stream()
                .map(OntologyHelper::getOntologyUri)
                .collect(Collectors.toList());

        if(ontologyNames.contains(ontologyURI)){
            throw new OntologyDuplicationException(ontologyURI + " ontology already exists");
        }

        List<String> directoryFiles = ScriptDAO.getScripts(directory).stream().map(File::getName).collect(Collectors.toList());
        if(directoryFiles.contains(filename)){
            throw new FileExistsException(filename + " already exists");
        }

        String lines = Files.toString(template, Charsets.UTF_8).replace("ONTOLOGY_NAME", ontologyURI.toString());
        File file = new File(directory + "/" + filename);
        CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
        sink.write(lines);
    }

    public void removeScriptOntology(String scriptPath, String ontology) throws FileNotFoundException {
        Model ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        ontModel.removeAll(ontModel.getResource(ontology), OWL.imports, null);
        ontModel.removeAll(null, OWL.imports, ontModel.getResource(ontology));
        FileOutputStream os = new FileOutputStream(scriptPath);
        ontModel.write(os, FileUtils.langTurtle);
    }

    public void addScriptOntology(String scriptPath, String ontologyName) throws MissingOntologyException, FileNotFoundException {
        File f = new File(scriptPath);
        Model defaultModel = ontologyHelper.createOntModel(f);
        List<Statement> statements = defaultModel
                .read(f.getAbsolutePath(), org.apache.jena.util.FileUtils.langTurtle)
                .listStatements(null, OWL.imports, (RDFNode) null).toList();

        if(statements.size() == 0){
            throw new MissingOntologyException("Script does not contain ontology.");
        }

        Model resModel = ontologyHelper.createOntModel(f);
        Statement ontology = statements.get(0);
        resModel.add(ontology.getSubject(), OWL.imports, new ResourceImpl(ontologyName));
        FileOutputStream os = new FileOutputStream(scriptPath);
        resModel.write(os, FileUtils.langTurtle);
    }

    public List<String> getScriptImportedOntologies(String scriptPath) {
        File f = new File(scriptPath);
        Model defaultModel = ModelFactory.createDefaultModel();
        List<Statement> statements = defaultModel
                .read(f.getAbsolutePath(), org.apache.jena.util.FileUtils.langTurtle)
                .listStatements(null, OWL.imports, (RDFNode) null).toList();

        return statements.stream().map(x -> x.getObject().toString()).collect(Collectors.toList());
     }

}
