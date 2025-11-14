package og_spipes.service;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import cz.cvut.spipes.util.JenaUtils;
import og_spipes.model.Vocabulary;
import og_spipes.model.dto.ScriptFunctionArgument;
import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.persistence.dao.OntologyDao;
import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.rest.exception.ModuleDependencyException;
import og_spipes.service.exception.FileExistsException;
import og_spipes.service.exception.MissingOntologyException;
import og_spipes.service.exception.OntologyDuplicationException;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.OWL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.apache.jena.util.FileUtils.langTurtle;

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

    public void createDependency(String scriptPath, String from, String to) throws IOException {
        Model ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        List<Resource> ontModelResources = ontModel.listSubjects().toList().stream().filter(Objects::nonNull).filter(x -> x.getURI() != null).collect(Collectors.toList());
        Optional<Resource> moduleFrom = ontModelResources.stream().filter(x -> x.getURI().equals(from)).findAny();
        Optional<Resource> moduleTo = ontModelResources.stream().filter(x -> x.getURI().equals(to)).findAny();

        if(!moduleFrom.isPresent()){
            throw new ModuleDependencyException("Unable to create dependency" + "<" + from + ", " + to +">. Resource " + from + "is not present in model loaded from script " + scriptPath, scriptPath, null, to, from);
        }
        if(!moduleTo.isPresent()){
            throw new ModuleDependencyException("Unable to create dependency" + "<" + from + ", " + to +">. Resource " + to + "is not present in model loaded from script " + scriptPath, scriptPath, null, to, from);
        }

        removeInverseDependencyIfExists(ontModel, moduleFrom.get(), moduleTo.get(), scriptPath);

        Statement dependency = ontModel.createStatement(moduleFrom.get(), ResourceFactory.createProperty(Vocabulary.s_p_next), moduleTo.get());
        ontModel.add(dependency);

        try {
            JenaUtils.writeScript(Path.of(scriptPath), ontologyHelper.getBaseModel(ontModel));
        }
        catch (IllegalStateException e) {
            throw new ModuleDependencyException("Unable to create dependency" + "<" + from + ", " + to +">. Since it will create cycle in the pipeline " + scriptPath, scriptPath, null, to, from);
        }
    }

    public void deleteDependency(String scriptPath, String from, String to) throws IOException {
        Model ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        List<Resource> ontModelResources = ontModel.listSubjects().toList().stream().filter(Objects::nonNull).filter(x -> x.getURI() != null).collect(Collectors.toList());
        Optional<Resource> moduleFrom = ontModelResources.stream().filter(x -> x.getURI().equals(from)).findAny();
        Optional<Resource> moduleTo = ontModelResources.stream().filter(x -> x.getURI().equals(to)).findAny();


        if(!moduleFrom.isPresent()){
            throw new ModuleDependencyException("Unable to delete dependency" + "<" + from + ", " + to +">. Resource " + from + "is not present in model loaded from script " + scriptPath, scriptPath, null, to, from);
        }
        if(!moduleTo.isPresent()){
            throw new ModuleDependencyException("Unable to delete dependency" + "<" + from + ", " + to +">. Resource " + to + "is not present in model loaded from script " + scriptPath, scriptPath, null, to, from);
        }

        Model model = ontologyHelper.getBaseModel(ontModel);
        Statement dependency = model.createStatement(moduleFrom.get(), ResourceFactory.createProperty(Vocabulary.s_p_next), moduleTo.get());

        if (!model.contains(dependency)) { // Dependency is not defined in base model
            String subscriptPath = (ontologyHelper.getStatementOriginScriptPath(dependency, ontModel, scriptPath, from, to)).getAbsolutePath();
            String message = "Dependency \n" +
                    dependency.toString() + "\n" +
                    "was not found in opened script \n" +
                    scriptPath + "\n" +
                    "but was found in subscript \n" +
                    subscriptPath + "\n" +
                    "Please remove the dependency from the subscript first.";
            throw new ModuleDependencyException(message, scriptPath, subscriptPath, to, from);
        }

        ontModel.remove(dependency);
        JenaUtils.writeScript(Path.of(scriptPath), ontologyHelper.getBaseModel(ontModel));
    }

    public void deleteModule(String scriptPath, String module) throws IOException {
        Model ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        List<Statement> fromStatements = OntologyHelper.getAllStatementsRecursively(ontModel, module);
        ontModel.remove(fromStatements);
        ontModel.removeAll(null, null, ontModel.getResource(module));
        JenaUtils.writeScript(Path.of(scriptPath), ontologyHelper.getBaseModel(ontModel));
    }

    public void deleteModuleOnly(String scriptPath, String module) throws IOException {
        Model ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        List<Statement> fromStatements = OntologyHelper.getAllStatementsRecursively(ontModel, module);
        ontModel.remove(fromStatements);
        JenaUtils.writeScript(Path.of(scriptPath), ontologyHelper.getBaseModel(ontModel));
    }

    public void moveModule(String scriptFrom, String scriptTo, String moduleURI, boolean renameBaseOntology) throws IOException {
        String fromOntology = OntologyDao.getOntologyUri(new File(scriptFrom));
        String toOntology = OntologyDao.getOntologyUri(new File(scriptTo));

        File fromFile = new File(scriptFrom);
        Model fromModel = ModelFactory.createDefaultModel().read(fromFile.getAbsolutePath(), langTurtle);

        Resource resource = fromModel.getResource(moduleURI);
        Model model = ResourceUtils.reachableClosure(resource);

        //should be transactional
        File toFile = new File(scriptTo);
        Model toModel = ModelFactory.createDefaultModel().read(toFile.getAbsolutePath(), langTurtle);
        toModel.add(model);
        if(renameBaseOntology){
            toModel.listSubjects().forEachRemaining(s -> {
                if(s.toString().contains(fromOntology)){
                    String replaced = s.toString().replace(fromOntology, toOntology);
                    ResourceUtils.renameResource(s, replaced);
                }
            });
        }
        JenaUtils.writeScript(Path.of(scriptTo), toModel);
        toModel.close();

        deleteModuleOnly(scriptFrom, moduleURI);

        //rename old prefix to new one in all files
        if(renameBaseOntology){
            String renamedModuleUri = moduleURI.replace(fromOntology, toOntology);
            for(File file : scriptDao.getScripts()){
                Model resModel = ModelFactory.createDefaultModel().read(file.getAbsolutePath(), langTurtle);
                AtomicBoolean changed = new AtomicBoolean(false);
                resModel.listSubjects().toList().forEach(s -> {
                    if(!s.isAnon()){
                        String replaced = s.toString().replace(moduleURI, renamedModuleUri);
                        if(!replaced.equals(s.toString())){
                            changed.set(true);
                        }
                        ResourceUtils.renameResource(s, replaced);
                    }
                });
                resModel.listStatements().toList().forEach(s -> {
                    RDFNode rdfNode = s.getObject();
                    if(!rdfNode.isAnon() && rdfNode.toString().equals(moduleURI)){
                        changed.set(true);
                        String replaced = rdfNode.toString().replace(moduleURI, renamedModuleUri);
                        ResourceUtils.renameResource(rdfNode.asResource(), replaced);
                    }
                });
                if(changed.get()){
                    JenaUtils.writeScript(file.toPath(), resModel);
                    resModel.close();
                }
            }
        }
        //TODO resolve imports
    }

    public void createScript(String directory, String filename, URI ontologyURI, String returnModuleName, String functionName, List<ScriptFunctionArgument> scriptFunctionArguments) throws IOException, OntologyDuplicationException, FileExistsException {

        List<String> ontologyNames = scriptDao.getScripts().stream()
                .map(OntologyDao::getOntologyUri)
                .collect(Collectors.toList());

        if(ontologyNames.contains(ontologyURI)){
            throw new OntologyDuplicationException(ontologyURI + " ontology already exists");
        }

        List<String> directoryFiles = ScriptDAO.getScripts(directory).stream().map(File::getName)
                .collect(Collectors.toList());
        if (directoryFiles.contains(filename)) {
            throw new FileExistsException(filename + " already exists");
        }

        boolean isWithoutFunctions = (returnModuleName == null && functionName == null);

        String templateScript = isWithoutFunctions
                ? "template/template-script-without-functions.sms.ttl"
                : "template/template-script-with-functions.sms.ttl";

        InputStream in = getClass().getClassLoader().getResourceAsStream(templateScript);
        if (in == null) {
            throw new FileNotFoundException("Template file not found: " + templateScript);
        }

        String content = new String(in.readAllBytes(), Charsets.UTF_8)
                .replace("ONTOLOGY_IRI", ontologyURI.toString());

        if (!isWithoutFunctions) {
            content = content.replace("RETURN_MODULE_NAME", returnModuleName)
                    .replace("FUNCTION_NAME", functionName);

            StringBuilder argumentsBuilder = new StringBuilder();
            for (ScriptFunctionArgument arg : scriptFunctionArguments) {
                argumentsBuilder.append("  spin:constraint [\n");
                argumentsBuilder.append("    rdf:type spl:Argument ;\n");
                argumentsBuilder.append("    spl:predicate :").append(arg.getName()).append(" ;\n");
                argumentsBuilder.append("    rdfs:label \"").append(arg.getLabel()).append("\" ;\n");
                argumentsBuilder.append("    rdfs:comment \"").append(arg.getComment()).append("\" ;\n");

                String argString = argumentsBuilder.toString();
                argumentsBuilder = new StringBuilder(argString);
                argumentsBuilder.append("  ] ;\n");
            }

            String arguments = argumentsBuilder.toString();
            if (!arguments.isEmpty()) {
                content = content.replace("FUNCTION_ARGUMENTS", arguments);
            } else {
                content = content.replace("FUNCTION_ARGUMENTS", "");
            }
        } else {
            content = content.replace("FUNCTION_ARGUMENTS", "");
        }

        File file = new File(directory + "/" + filename);
        CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
        sink.write(content);
    }

    public void removeScriptOntology(String scriptPath, String ontology) throws IOException {
        Model ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        ontModel.removeAll(ontModel.getResource(ontology), OWL.imports, null);
        ontModel.removeAll(null, OWL.imports, ontModel.getResource(ontology));
        JenaUtils.writeScript(Path.of(scriptPath), ontologyHelper.getBaseModel(ontModel));
    }

    public void addScriptOntology(String scriptPath, String ontologyName) throws MissingOntologyException, IOException {
        File f = new File(scriptPath);

        String ontology = OntologyDao.getOntologyUri(f);
        if(ontology == null){
            throw new MissingOntologyException("Script does not contain ontology.");
        }

        Model resModel = ontologyHelper.createOntModel(f);
        resModel.add(ResourceFactory.createResource(ontology), OWL.imports, ResourceFactory.createResource(ontologyName));
        JenaUtils.writeScript(Path.of(scriptPath), resModel);
    }

    public List<String> getScriptImportedOntologies(String scriptPath) {
        File f = new File(scriptPath);
        List<String> imports = OntologyDao.getOntologyImports(f);

        return imports.stream()
                .filter(x -> !x.equals("http://onto.fel.cvut.cz/ontologies/s-pipes-lib"))
                .collect(Collectors.toList());
    }

    private void removeInverseDependencyIfExists(Model ontModel, Resource from, Resource to, String scriptPath) {
        Statement inverse = ontModel.createStatement(to, ResourceFactory.createProperty(Vocabulary.s_p_next), from);
        Model baseModel = ontologyHelper.getBaseModel(ontModel);

        if (ontModel.contains(inverse)) {
            if (!baseModel.contains(inverse)) {
                String subscriptPath = ontologyHelper.getStatementOriginScriptPath(inverse, ontModel, scriptPath, from.toString(), to.toString()).getAbsolutePath();
                String message = "Unable to remove inverse dependency. Dependency \n" +
                        inverse.toString() + "\n" +
                        "was not found in opened script \n" +
                        scriptPath + "\n" +
                        "but was found in subscript \n" +
                        subscriptPath + "\n" +
                        "Please remove the dependency from the subscript first.";

                throw new ModuleDependencyException(message, scriptPath, subscriptPath, to.getURI(), from.getURI());
            }
            ontModel.remove(inverse);
        }
    }
}