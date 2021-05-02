package og_spipes.persistence.dao;

import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import cz.cvut.kbss.ontodriver.jena.JenaDataSource;
import cz.cvut.kbss.ontodriver.jena.config.JenaOntoDriverProperties;
import og_spipes.model.Vocabulary;
import og_spipes.model.spipes.FunctionDTO;
import og_spipes.model.spipes.Module;
import og_spipes.model.spipes.ModuleType;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static og_spipes.model.Vocabulary.s_c_Modules;

@Repository
public class ScriptDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptDAO.class);

    private final String repositoryURL;

    //TODO consider if always create a new EM!
    final EntityManagerFactory emf;

    //TODO try to use created EM - check if working while editing
    public ScriptDAO(@Value("${repositoryUrl}") String repositoryURL) {
        final Map<String, String> props = new HashMap<>();
        // Here we set up basic storage access properties - driver class, physical location of the storage
        props.put(JOPAPersistenceProperties.ONTOLOGY_PHYSICAL_URI_KEY, "local://temporary"); // jopa uses the URI scheme to choose between local and remote repo, file and (http, https and ftp)resp.
        props.put(JOPAPersistenceProperties.ONTOLOGY_URI_KEY, "http://temporary"); // jopa uses the URI scheme to choose between local and remote repo, file and (http, https and ftp)resp.
        props.put(JOPAPersistenceProperties.DATA_SOURCE_CLASS, JenaDataSource.class.getName());
        // Ontology language
        props.put(JOPAPersistenceProperties.LANG, "en");
        // Where to look for entities
        props.put(JOPAPersistenceProperties.SCAN_PACKAGE, "og_spipes.model");
        // Persistence provider name
        props.put(JOPAPersistenceProperties.JPA_PERSISTENCE_PROVIDER, JOPAPersistenceProvider.class.getName());
        props.put(JenaOntoDriverProperties.IN_MEMORY, "true");

        this.emf = Persistence.createEntityManagerFactory("og_spipesPU", props);
        this.repositoryURL = repositoryURL;
    }

    public List<ModuleType> getModuleTypes(Model m) {
        EntityManager em = emf.createEntityManager();
        InfModel infModel = ModelFactory.createRDFSModel(m);
        Dataset dataset = em.unwrap(Dataset.class);
        dataset.setDefaultModel(infModel);
        emf.getCache().evict(ModuleType.class);
        List<ModuleType> moduleTypes = em.createNativeQuery("select ?s where { ?s a ?type . }", ModuleType.class)
                .setParameter("type", URI.create(Vocabulary.s_c_Module)).getResultList();
        em.close();
        return moduleTypes;
    }

    public List<Module> getModules(Model m) {
        EntityManager em = emf.createEntityManager();
        InfModel infModel = ModelFactory.createRDFSModel(m);
        Dataset dataset = em.unwrap(Dataset.class);
        dataset.setDefaultModel(infModel);
        emf.getCache().evict(Module.class);

        List<Module> modules = em.createNativeQuery("select ?s where { ?s a ?type }", Module.class)
                .setParameter("type", URI.create(s_c_Modules)).getResultList();

        System.out.println("total modules count: " + modules.size());

        for(Module module : modules){
            System.out.println(module.getTypes());
            System.out.println(module.getNodeX());
            System.out.println(module.getNodeY());
            System.out.println(module.getSource());
//            module.getProperties().forEach((k, v) -> {
//                System.out.println(v);
//                v.forEach(x -> {
//                    System.out.println(x.toString());
//                });
//            });
            List<ModuleType> ts = em.createNativeQuery(
                    "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n" +
                            "           \n" +
                            "           select distinct ?type where {\n" +
                            "             ?module a ?type .\n" +
                            "             filter not exists {\n" +
                            "               ?module  a ?subtype .\n" +
                            "               ?subtype rdfs:subClassOf ?type .\n" +
                            "               filter ( ?subtype != ?type )\n" +
                            "             }\n" +
                            "           }",
                    ModuleType.class
            ).setParameter("module", module.getUri()).getResultList();
            if(!ts.isEmpty()){
                if(ts.size() > 1) LOG.warn("MORE TYPES FOUND!!!");
                module.setSpecificType(ts.get(0));
            }else{
                LOG.warn("NO TYPE FOUND!!!");
            }
        }
        em.close();
        return modules;
    }

    public StmtIterator getFunctionStatements(Model m) {
        return m.listStatements(null, RDF.type, m.createResource("http://topbraid.org/sparqlmotion#Function"));
    }

    public List<File> getScripts() {
        File rootFolder = new File(repositoryURL);
        return new ArrayList<>(FileUtils.listFiles(rootFolder, new String[]{"ttl"}, true));
    }

    public File findScriptByOntologyName(String ontologyName) throws IOException {
        File script = null;
        for(File f: getScripts()){
            if(FileUtils.readFileToString(f, "UTF-8").contains("<" + ontologyName + ">")){
                if(script == null){
                    script = f;
                }else{
                    LOG.warn("Two files with same ontology founded in {}", script.getAbsolutePath());
                }
            }
        }
        if(script == null){
            throw new IOException("File with onlogy: " + ontologyName + " not found");
        }
        return script;
    }

    public List<FunctionDTO> moduleFunctions(Model m) {
        List<FunctionDTO> functionDTOS = getFunctionStatements(m).toList().stream().map(statement -> {
            Resource subject = statement.getSubject();
            Set<String> statements = subject.listProperties(RDFS.comment).toList().stream().map(Statement::getString).collect(Collectors.toSet());
            return new FunctionDTO(subject.getURI(), subject.getLocalName(), statements);
        }).collect(Collectors.toList());
        return functionDTOS;
    }

}
