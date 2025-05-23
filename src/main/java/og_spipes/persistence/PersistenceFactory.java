/**
 * Copyright (C) 2019 Czech Technical University in Prague
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package og_spipes.persistence;

import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import cz.cvut.kbss.ontodriver.config.OntoDriverProperties;
import cz.cvut.kbss.ontodriver.rdf4j.Rdf4jDataSource;
import og_spipes.utils.SPipesEngineClasspathScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class PersistenceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceFactory.class);

    private final Environment environment;

    private EntityManagerFactory emf;

    @Autowired
    public PersistenceFactory(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    private void init() {
        final String scriptPath = environment.getProperty("scriptPaths");
        LOG.info("Using repository path: {}.", scriptPath);

        final Map<String, String> props = new HashMap<>();
        // Here we set up basic storage access properties - driver class, physical location of the storage
        props.put(JOPAPersistenceProperties.ONTOLOGY_PHYSICAL_URI_KEY, scriptPath);
        props.put(JOPAPersistenceProperties.DATA_SOURCE_CLASS, Rdf4jDataSource.class.getName());
//        // Let's use Jena TDB for storage
//        props.put(JenaOntoDriverProperties.JENA_STORAGE_TYPE, repoType);
//        // Use Jena's rule-based RDFS reasoner
//        props.put(OntoDriverProperties.REASONER_FACTORY_CLASS, RDFSRuleReasonerFactory.class.getName());
        // View transactional changes during transaction
        props.put(OntoDriverProperties.USE_TRANSACTIONAL_ONTOLOGY, Boolean.TRUE.toString());
        // Where to look for entities
        props.put(JOPAPersistenceProperties.SCAN_PACKAGE, "og_spipes.model");
        // Ontology language
        props.put(JOPAPersistenceProperties.LANG, "en");
        // Persistence provider name
        props.put(JOPAPersistenceProperties.JPA_PERSISTENCE_PROVIDER, JOPAPersistenceProvider.class.getName());

        props.put(JOPAPersistenceProperties.CLASSPATH_SCANNER_CLASS, SPipesEngineClasspathScanner.class.getName());
        this.emf = Persistence.createEntityManagerFactory("og_spipesPU", props);
    }

    @Bean
    @Qualifier("localEM")
    public EntityManagerFactory entityManagerFactory() {
        return emf;
    }

    @PreDestroy
    public void close() {
        emf.close();
    }
}
