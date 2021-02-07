package og_spipes.service;

import og_spipes.model.spipes.ModuleType;
import og_spipes.persistence.dao.ScriptDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ScriptServiceTest {

    @Mock
    private ScriptDao scriptDao;

    @Mock
    private OntologyHelper ontologyHelper;

    @InjectMocks
    private ScriptService scriptService;

    @Test
    public void getModuleTypes() {
        ArrayList<ModuleType> mockModuleTypes = new ArrayList<>();
        mockModuleTypes.add(new ModuleType());
        when(scriptDao.getModuleTypes(any())).thenReturn(mockModuleTypes);

        List<ModuleType> moduleTypes = scriptService.getModuleTypes("/dummy.ttl");
        assertEquals(1, moduleTypes.size());
    }

}