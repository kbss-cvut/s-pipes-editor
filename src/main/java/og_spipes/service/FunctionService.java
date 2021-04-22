package og_spipes.service;

import og_spipes.model.spipes.FunctionDTO;
import og_spipes.persistence.dao.ScriptDAO;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunctionService {

    private final ScriptDAO scriptDao;

    @Autowired
    public FunctionService(ScriptDAO scriptDao) {
        this.scriptDao = scriptDao;
    }

    public List<FunctionDTO> moduleFunctions(String scriptPath) {
        return scriptDao.moduleFunctions(ModelFactory.createDefaultModel().read(scriptPath));
    }

}
