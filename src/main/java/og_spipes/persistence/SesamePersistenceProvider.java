package og_spipes.persistence;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import og_spipes.model.spipes.TestJSONLD;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.URI;

@Configuration
public class SesamePersistenceProvider {

    private static final Logger log = LoggerFactory.getLogger(SesamePersistenceProvider.class);

    private final String repositoryUrl;

    private final String repositoryName;

    private final EntityManagerFactory emf;

    private Repository repository;

    @Autowired
    public SesamePersistenceProvider(@Value("${sesame.repositoryUrl}") String repositoryUrl, @Value("${sesame.repositoryName}") String repositoryName, @Qualifier("sesameEMF") EntityManagerFactory emf) {
        this.repositoryUrl = repositoryUrl;
        this.repositoryName = repositoryName;
        this.emf = emf;
    }

    @Bean
    public Repository repository() {
        return repository;
    }

    @PostConstruct
    private void initializeStorage() {
        forceRepoInitialization();
        final String repoUrl = repositoryUrl + "/" + repositoryName;
        try {
            this.repository = RepositoryProvider.getRepository(repoUrl);
            assert repository.isInitialized();
        } catch (RepositoryException | RepositoryConfigException e) {
            log.error("Unable to connect to Sesame repository at " + repoUrl, e);
        }
    }

    private void forceRepoInitialization() {
        final EntityManager em = emf.createEntityManager();
        try {
            // The URI doesn't matter, we just need to trigger repository connection initialization
            em.find(TestJSONLD.class, URI.create("http://unknown"));
        } finally {
            em.close();
        }
    }
}