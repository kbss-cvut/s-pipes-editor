package og_spipes.service;

import og_spipes.model.spipes.FunctionDTO;
import og_spipes.persistence.dao.ScriptDao;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import org.eclipse.rdf4j.query.algebra.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FunctionService {

    private static final Logger LOG = LoggerFactory.getLogger(FunctionService.class);

    private final ScriptDao scriptDao;

    @Autowired
    public FunctionService(ScriptDao scriptDao) {
        this.scriptDao = scriptDao;
    }

    public List<FunctionDTO> moduleFunctions(String scriptPath) {
        return scriptDao.moduleFunctions(ModelFactory.createDefaultModel().read(scriptPath));
    }

}
