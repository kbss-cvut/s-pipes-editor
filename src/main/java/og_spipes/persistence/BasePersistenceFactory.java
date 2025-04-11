package og_spipes.persistence;

import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import og_spipes.utils.SPipesEngineClasspathScanner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BasePersistenceFactory {

    private final String repositoryUrl;
    private final String repositoryName;
    private final String driver;

    private EntityManagerFactory emf;

    public BasePersistenceFactory(
            @Value("${rdf4j.repositoryUrl}") String repositoryUrl,
            @Value("${rdf4j.repositoryName}") String repositoryName,
            @Value("${rdf4j.driver}") String driver
    ) {
        this.repositoryUrl = repositoryUrl;
        this.repositoryName = repositoryName;
        this.driver = driver;
    }

    @Bean
    @Qualifier("rdf4jEMF")
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    @Bean
    @Qualifier("rdf4jEM")
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @PostConstruct
    private void init() {
        final Map<String, String> properties = new HashMap<>();
        properties.put(JOPAPersistenceProperties.ONTOLOGY_PHYSICAL_URI_KEY, repositoryUrl + "/" + repositoryName);
        properties.put(JOPAPersistenceProperties.DATA_SOURCE_CLASS, driver);
        properties.put(JOPAPersistenceProperties.SCAN_PACKAGE, "og_spipes.model");
        properties.put(JOPAPersistenceProperties.JPA_PERSISTENCE_PROVIDER, JOPAPersistenceProvider.class.getName());
        properties.put(JOPAPersistenceProperties.CLASSPATH_SCANNER_CLASS, SPipesEngineClasspathScanner.class.getName());
        this.emf = Persistence.createEntityManagerFactory("og_spipes_rdf4j", properties);
    }

    @PreDestroy
    private void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}