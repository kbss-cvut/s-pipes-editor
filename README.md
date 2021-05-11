# S-Pipes Editor Backend

TODO
## Features

TODO

### Dockerization

The docker image of SPipes Editor Backend can be built by `docker build -t s-pipes-editor-rest .`

Then, SPipes Editor Backend can be run as `docker run -p 18115:18115 s-pipes-editor-rest`   

The list of all configurable parameters which could be override by enviroment `-e` variables could be found in `application.properties`. Most important ones are:
* SCRIPTPATHS - The location of the SPipes scripts 
* ENGINEURL - SPipes engine URL
* RDF4J_REPOSITORYURL - RDF4j repository URL 
* RDF4J_REPOSITORYNAME - RDF4j repository name
* RDF4J_PCONFIGURL - Override spies-engine logging configuration 


### Docker-compose
The docker-compose is composed of 3 services:
* s-pipes-editor-rest - accessible on `http://localhost:18115`
* s-pipes-engine - manual build of the image is required [repository](https://github.com/kbss-cvut/s-pipes)  - accessible on `http://localhost:8081`
* rdf4j - official [docker image](https://hub.docker.com/r/eclipse/rdf4j-workbench) - accessible on `http://localhost:8080/rdf4j-workbench`

Manual required steps:
* s-pipes-engine
    * The service does not automatically create the repository in RDF4J, so manual creation of a repository is required.
    * The logging configuration for RDF4j is hardcoded in the image, but it could override via `_pConfigURL` param. However, it is not a convenient format to work. Also both servies must to share volume or the config has to be exposed.

