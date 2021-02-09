package og_spipes.service;

import og_spipes.model.Vocabulary;
import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import og_spipes.persistence.dao.ScriptDao;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScriptService {

    private final ScriptDao scriptDao;
    private final OntologyHelper ontologyHelper;

    @Autowired
    public ScriptService(ScriptDao scriptDao, OntologyHelper ontologyHelper) {
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

        Model model = ModelFactory.createDefaultModel().read(scriptPath);
        model.add(moduleFrom.get(), new PropertyImpl(Vocabulary.s_p_next), moduleTo.get());
        FileOutputStream os = new FileOutputStream(scriptPath);
        model.write(os, FileUtils.langTurtle);
    }

    public void deleteDependency(String scriptPath, String from, String to) {
//        log.info(f"""Deleting dependency from $from to $to in $scriptPath""")
//        helper.getFileDefiningTriple(from, Vocabulary.s_p_next, to)(new File(scriptPath)) match {
//            case Success(file) =>
//                val m = ModelFactory.createDefaultModel().read(file)
//                cleanly(new FileOutputStream(file))(_.close())(os => {
//                    m.removeAll(m.getResource(from), new PropertyImpl(Vocabulary.s_p_next), m.getResource(to)).write(os, FileUtils.langTurtle)
//            })
//          .map(_ => NotificationController.notify(scriptPath))
//            case failure => failure
//        }
    }

    public void deleteModule(String scriptPath, String module) {
//        log.info(f"""Deleting module $module from $scriptPath""")
//        helper.getFileDefiningSubject(module)(new File(scriptPath)) match {
//            case Success(file) =>
//                val m = ModelFactory.createDefaultModel().read(file)
//                m.removeAll(m.getResource(module), null, null)
//                m.removeAll(null, null, m.getResource(module))
//                cleanly(new FileOutputStream(file))(_.close())(os => {
//                    m.write(os, FileUtils.langTurtle)
//            })
//          .map(_ => NotificationController.notify(scriptPath))
//            case f => f
//        }
    }

}
