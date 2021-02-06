package og_spipes.persistence.dao;

import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import cz.cvut.kbss.ontodriver.config.OntoDriverProperties;
import cz.cvut.kbss.ontodriver.jena.JenaDataSource;
import cz.cvut.kbss.ontodriver.jena.config.JenaOntoDriverProperties;
import og_spipes.model.Vocabulary;
import og_spipes.model.spipes.ModuleType;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ScriptDao {


    //TODO parametrize later - root folder
    private final File rootFolder = new File("/home/chlupnoha/IdeaProjects/og-spipes/src/test/resources/scripts_test/sample");

    final EntityManagerFactory emf;

    public ScriptDao() {
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

    public List<File> getScripts() {
        return new ArrayList<>(FileUtils.listFiles(rootFolder, new String[]{"ttl"}, true));
    }

}
