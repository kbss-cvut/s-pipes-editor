package og_spipes.service;

import cz.cvut.spipes.util.JenaUtils;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import og_spipes.model.spipes.ExecutionDTO;
import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.persistence.dao.TransformationDAO;
import og_spipes.service.exception.SPipesEngineException;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SPipesExecutionService {

    private static final Logger LOG = LoggerFactory.getLogger(SPipesExecutionService.class);

    private final String engineUrl;

    private final String pConfigURL;

    private final String workbenchRepo;

    private final TransformationDAO transformationDAO;

    private final ScriptDAO scriptDAO;

    @Autowired
    public SPipesExecutionService(
            @Value("${engineurl}") String engineUrl,
            @Value("${rdf4j.pConfigURL}") String pConfigURL,
            @Value("${rdf4j.workbenchUrlRepository}") String workbenchRepo,
            TransformationDAO transformationDAO,
            ScriptDAO scriptDAO
    ) {
        this.engineUrl = engineUrl;
        this.pConfigURL = pConfigURL;
        this.workbenchRepo = workbenchRepo;
        this.transformationDAO = transformationDAO;
        this.scriptDAO = scriptDAO;
    }

    public String serviceExecution (
            String functionId,
            Map<String, String> params,
            String scriptPath
    ) throws SPipesEngineException {
        String serviceUrl = engineUrl + "/service";
        params.put("_pId", functionId);
        params.put("_pConfigURL", pConfigURL);
        params.put("_pScriptPath", scriptPath);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl);
        for (Map.Entry<String, String> pair : params.entrySet()) {
            builder.queryParam(pair.getKey(), pair.getValue());
        }

        LOG.info("SPipes engine query: " + builder.build());
        HttpResponse<String> request = Unirest.get(builder.build().toString()).asString();
        if(request.getStatus() != 200){
            LOG.warn(request.getBody());
            throw new SPipesEngineException("SPipes engine error.");
        }
        return request.getBody();
    }

    private void createDebugConfig(String configLocation, String moduleScript){
        OntDocumentManager documentManager = OntDocumentManager.getInstance();
        documentManager.setReadFailureHandler((s, model, e) -> LOG.debug(s + "; " +e.getLocalizedMessage()));
        OntModel model = documentManager.getOntology(pConfigURL, OntModelSpec.OWL_MEM);
        OntModel model2 = documentManager.getOntology(moduleScript, OntModelSpec.OWL_MEM);
        model.add(model2);

        File file = new File(configLocation);
        try (OutputStream os = new FileOutputStream(file)){
            JenaUtils.writeScript(os, model);
            LOG.info("Debug config created");
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }

    }

    public String moduleExecution(String moduleScript, String moduleInput, String moduleId, Map<String, String> params) throws IOException, SPipesEngineException {
        String configLocation = File.createTempFile("config", ".ttl").getAbsolutePath();
        LOG.info("ConfigLocation: " + configLocation);
        String serviceUrl = engineUrl + "/module";
        params.put("_pId", moduleId);
        createDebugConfig(configLocation, moduleScript);
        params.put("_pConfigURL", configLocation);
        params.put("_pScriptPath", moduleScript);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl);
        for (Map.Entry<String, String> pair : params.entrySet()) {
            builder.queryParam(pair.getKey(), pair.getValue());
        }

        LOG.info("SPipes engine query: " + builder.build());
        String nonEmptyInput = moduleInput.isEmpty() ? "\n" : moduleInput;
        HttpResponse<String> response = Unirest.post(builder.build().toString())
                .header("content-type", "text/turtle")
                .body(nonEmptyInput)
                .asString();

        if(response.getStatus() != 200){
            LOG.warn(response.getBody());
            throw new SPipesEngineException("SPipes engine error.");
        }

        return response.getBody();
    }

    /**
     * Mapping is not working correctly so the complicated mapping has to be done
     * @return
     */
    public List<ExecutionDTO> getAllExecution() {
        return transformationDAO.getAllExecutionTransformation().stream()
                .filter(x -> x.getProperties().containsKey("http://onto.fel.cvut.cz/ontologies/s-pipes/has-script"))
                .map(x -> {
                    Map<String, Set<Object>> properties = x.getProperties();
                    String pipelineURI = properties.get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-script").stream().findFirst().orElse("").toString();
                    String name = pipelineURI.substring(pipelineURI.lastIndexOf("/")+1);
                    String duration = properties.get("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-duration").stream().findFirst().orElse("").toString();
                    Date startDate = x.getStart_date();
                    Date finishDate = x.getFinish_date();
                    try {
                        return new ExecutionDTO(
                                pipelineURI,
                                name,
                                scriptDAO.findScriptByOntologyName(pipelineURI).getAbsolutePath(),
                                Long.parseLong(duration),
                                startDate,
                                finishDate,
                                x.getId(),
                                workbenchRepo + "/explore?resource=<" + x.getId() + ">"
                        );
                    } catch (IOException e) {
                        LOG.warn(e.getMessage());
                        return null;
                    }
        }).collect(Collectors.toList());
    }

    public String getPipelineName(String id) {
        return transformationDAO.getPipelineName(id);
    }
}