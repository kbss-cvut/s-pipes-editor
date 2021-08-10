/**
 * Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package og_spipes.persistence.dao;

import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.query.TypedQuery;
import og_spipes.model.Vocabulary;
import og_spipes.model.spipes.TransformationDTO;
import og_spipes.model.util.EntityToOwlClassMapper;
import og_spipes.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Base implementation of the generic DAO.
 */
public abstract class BaseDAO<T> {

    static final Logger LOG = LoggerFactory.getLogger(BaseDAO.class);

    protected EntityManager em;
    protected final Class<T> type;
    protected final URI typeUri;

    protected BaseDAO(EntityManager em, Class<T> type) {
        this.em = em;
        this.type = type;
        this.typeUri = URI.create(EntityToOwlClassMapper.getOwlClassForEntity(type));
    }

    public T find(URI uri) {
        Objects.requireNonNull(uri);
        return em.find(type, uri);
    }

    public List<T> find(Set<URI> uri) {
        Objects.requireNonNull(uri);
        List<String> collect = uri.stream().map(x -> "<" + x.toString() + ">").collect(Collectors.toList());
        String uris = StringUtils.join(collect, ',');
        //dunno how to pass uris
        return em.createNativeQuery("SELECT ?s WHERE {?s a ?type . FILTER(?s IN (" + uris +"))}", type)
                .setParameter("type", URI.create("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation"))
                .getResultList();
    }

    public T findByKey(String key) {
        try {
            return em.createNativeQuery("SELECT ?x WHERE { ?x a ?type; ?hasKey ?key . }", type)
                     .setParameter("type", typeUri)
                     .setParameter("hasKey", URI.create(Vocabulary.s_p_identifier))
                     .setParameter("key", key, "en")
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<T> findAll() {
        return em.createNativeQuery("SELECT ?x WHERE { ?x a ?type . }", type).setParameter("type", typeUri)
                 .getResultList();
    }

    public void persist(T entity) {
        Objects.requireNonNull(entity);
        try {
            em.persist(entity);
        } catch (Exception e) {
            LOG.error("Error when persisting entity.", e);
            throw new PersistenceException(e);
        }
    }

    public void persist(Collection<T> entities) {
        Objects.requireNonNull(entities);
        try {
            entities.forEach(this::persist);
        } catch (Exception e) {
            LOG.error("Error when persisting entities.", e);
            throw new PersistenceException(e);
        }
    }

    public void update(T entity) {
        Objects.requireNonNull(entity);
        try {
            em.merge(entity);
        } catch (Exception e) {
            LOG.error("Error when updating entity.", e);
            throw new PersistenceException(e);
        }
    }

    public void remove(T entity) {
        Objects.requireNonNull(entity);
        try {
            final T toRemove = em.merge(entity);
            assert toRemove != null;
            em.remove(toRemove);
        } catch (Exception e) {
            LOG.error("Error when removing entity.", e);
            throw new PersistenceException(e);
        }
    }

    public boolean exists(URI uri) {
        if (uri == null) {
            return false;
        }
        final String owlClass = type.getDeclaredAnnotation(OWLClass.class).iri();
        return em.createNativeQuery("ASK { ?individual a ?type . }", Boolean.class).setParameter("individual", uri)
                 .setParameter("type", URI.create(owlClass)).getSingleResult();
    }
}
