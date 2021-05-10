# S-Pipes Editor Backend

TODO
## Features

TODO

### Dockerization

The docker image of SPipes Editor Backend can be built by `docker build -t spipes-editor-rest: .`

Then, SPipes Editor Backend can be run as `docker run -p 18115:18115 spipes-editor-rest`   

The list of all configurable parameters which could be override by enviroment `-e` variables could be found in `application.properties`. Most important ones are:
* REPOSITORYURL - The location of the SPipes scripts 
* ENGINEURL - SPipes engine URL
* SESAME_REPOSITORYURL - RDF4j repository URL 
* SESAME_REPOSITORYNAME - RDF4j repository name
* SESAME_PCONFIGURL - Override spies-engine logging configuration 


### Docker-compose
The docker-compose is composed of 4 services:
* spipes-editor-rest
* spipes-engine - manual build of the image is required [repository](https://github.com/kbss-cvut/s-pipes)
* rdf4j - official [docker image](https://hub.docker.com/r/eclipse/rdf4j-workbench)

Manual required steps:
* spipes-engine
    * The service does not automatically create the repository in RDF4J, so manual creation of a repository is required.
    * The logging configuration for RDF4j is hardcoded in the image, but it could override via `_pConfigURL` param. However, it is not a convenient format to work. Also both servies must to share volume or the config has to be exposed.

