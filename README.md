# JOPA + Jena Driver + Polymorphism

This example showcases usage of JOPA with the Jena OntoDriver working on top of Jena TDB/Fuseki server.
It also uses a polymorphic object model.

## Features

* Spring boot application with REST services,
* Publishing data in JSON-LD,
* Consuming data in JSON-LD,
* Declarative transactions,
* Polymorphic object model,
* Basic RDFS inference.

### JSON-LD Support

The demo uses [JB4JSON-LD](https://github.com/kbss-cvut/jb4jsonld) to publish/consume JSON-LD data. More specifically,
we are using the [JB4JSON-LD Jackson](https://github.com/kbss-cvut/jb4jsonld-jackson) integration, which allows the
REST API to work with [Jackson](https://github.com/FasterXML/jackson) as most Spring applications do.


### Persistence Setup

The persistence is set up in `cz.cvut.kbss.jopa.example08.persistence.PersistenceFactory`. Repository type and location is specified
in `application.properties` - either Fuseki or TDB storage can be selected.

### Declarative Transactions

This demo makes use of the [JOPA-Spring-transaction](https://github.com/ledsoft/jopa-spring-transaction) library, 
which enables JOPA to be used together with Spring's declarative transactions. See the services for usage example.
To make the transactions work, it is necessary to instantiate the `JopaTransactionManager` and `DelegatingEntityManager` Spring beans.
See `cz.cvut.kbss.jopa.example08.config.PersistenceConfig` for reference.

## Running the Demo

To run the demo, `mvn spring-boot:run` can be used. The REST API is available at [http://localhost:18115/og_spipes/rest](http://localhost:18115/example08/rest)
(configured in `application.properties`).
