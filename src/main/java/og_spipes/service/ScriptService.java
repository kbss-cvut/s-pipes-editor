package og_spipes.service;

import og_spipes.model.Vocabulary;
import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.persistence.dao.ScriptDAO;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScriptService {

    private final ScriptDAO scriptDao;
    private final OntologyHelper ontologyHelper;

    @Autowired
    public ScriptService(ScriptDAO scriptDao, OntologyHelper ontologyHelper) {
        this.scriptDao = scriptDao;
        this.ontologyHelper = ontologyHelper;
    }

    public List<Module> getModules(String filepath){
        OntModel ontModel = ontologyHelper.createOntModel(new File(filepath));
        return scriptDao.getModules(ontModel);
    }

    public List<ModuleType> getModuleTypes(String filepath){
        OntModel ontModel = ontologyHelper.createOntModel(new File(filepath));
        return scriptDao.getModuleTypes(ontModel);
    }

    //TODO test later - quite hard now
    public void createDependency(String scriptPath, String from, String to) throws FileNotFoundException {
        OntModel ontModel = ontologyHelper.createOntModel(new File(scriptPath));
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
        OntModel ontModel = ontologyHelper.createOntModel(new File(scriptPath));
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
        OntModel ontModel = ontologyHelper.createOntModel(new File(scriptPath));
        ontModel.removeAll(ontModel.getResource(module), null, null);
        ontModel.removeAll(null, null, ontModel.getResource(module));
        FileOutputStream os = new FileOutputStream(scriptPath);
        ontModel.write(os, FileUtils.langTurtle);

        //TODO notification webhooks
    }

    public void moveModule(String scriptFrom, String scriptTo, String moduleURI) throws FileNotFoundException {
        //TODO add constrains - very problematic, could cause issues!

        OntModel fromModel = ontologyHelper.createOntModel(new File(scriptFrom));
        List<Statement> statements = fromModel.listStatements(fromModel.getResource(moduleURI), null, (RDFNode) null).toList();
        List<Statement> statements1 = fromModel.listStatements(null, null, fromModel.getResource(moduleURI)).toList();
        List<Statement> fromStatements = Stream.concat(statements.stream(), statements1.stream())
                .collect(Collectors.toList());

        OntModel toModel = ontologyHelper.createOntModel(new File(scriptTo));
        toModel.add(fromStatements);
        FileOutputStream os = new FileOutputStream(scriptTo);
        fromModel.write(os, FileUtils.langTurtle);

        deleteModule(scriptFrom, moduleURI);
    }

}
