# S-Pipes Editor Backend
Backend for SPipes editor UI

## Features
* Script creation
* Script validation
* Script execution
* Module debug

### Dockerization

The docker image of SPipes Editor Backend can be built by `docker build -t s-pipes-editor-rest .`

Then, SPipes Editor Backend can be run as `docker run -p 18115:18115 s-pipes-editor-rest`   

The list of all configurable parameters which could be override by enviroment `-e` variables could be found in `application.properties`. Most important ones are:
* SCRIPTPATHS - The location of the SPipes scripts
* SCRIPTRULES - SHACL rules.   
* ENGINEURL - SPipes engine URL
* RDF4J_REPOSITORYURL - RDF4j repository URL 
* RDF4J_REPOSITORYNAME - RDF4j repository name
* RDF4J_PCONFIGURL - Override spies-engine logging configuration 
* CONTEXTS_SCRIPTPATHS - Script location for SPipes engine, must be same as **SCRIPTPATHS**


### Docker-compose
The docker-compose is composed of 3 services and can be run via `docker-compose up`:
* chlupnoha/s-pipes-editor-rest:latest - [repository](https://hub.docker.com/repository/docker/chlupnoha/s-pipes-editor-rest) - accessible on `http://localhost:18115`
* chlupnoha/spipes-engine:latest - [repository](https://hub.docker.com/repository/docker/chlupnoha/spipes-engine) - accessible on `http://localhost:8081`
* eclipse/rdf4j-workbench:amd64-3.5.0 - official [docker image](https://hub.docker.com/r/eclipse/rdf4j-workbench) - accessible on `http://localhost:8080/rdf4j-workbench`

It is important to mention the UI for application is avaliable on [s-pipes-editor-ui](https://github.com/chlupnoha/s-pipes-editor-ui)

Manual required steps:
* s-pipes-engine
    * The service does not automatically create the repository in RDF4J, so manual creation of a repository is required (after running `docker-compose up`).
          * First open the RDF4J Workbench: `http://localhost:<port>/rdf4j-workbench` where `<port>` is the RDF4J service port specified in `docker-compose.yml`.
          * Then follow these instructions: [Creating a Repository](https://rdf4j.org/documentation/tools/server-workbench/#:~:text=for%20the%20repository.-,Creating%20a%20Repository,-Click%20on%20%E2%80%9CNew) (For repository type use for example Native Store.)
    * The logging configuration for RDF4j is hardcoded in the image, but it could override via `_pConfigURL` param. However, it is not a convenient format to work. Also both servies must to share volume or the config has to be exposed.
  
* Notes
  * volumes of `/tmp:/tmp` is currently required for sharing configuration for module debug
  * volumes of `/home:/home` is currently required for sharing the scripts
  * volumes of `/usr/local/tomcat/temp/` is currently required for sharing execution logs

![Architecture](architecture.png)
Architecture images

###Installation without Docker
Installation without Docker requires to download and setup [SPipes engine](https://github.com/kbss-cvut/s-pipes) with [RDF4J](https://rdf4j.org/download/). The 
required params are the same as the docker env variables. The installation of the engine is described in its README. 
The SPipes editor is the Spring boot application and could be run via the main method in Main. However, the editor
requires the running instance of SPipes engine and RDF4J. All necessary configuration is in `application.properties`. It
is important to mention the [UI](https://github.com/chlupnoha/s-pipes-editor-ui) exist.<br>
Nevertheless, the installation via Docker is recommended.

###License
Licensed under GPL v3.0.
