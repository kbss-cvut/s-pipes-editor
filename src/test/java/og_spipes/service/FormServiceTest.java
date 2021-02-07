package og_spipes.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class FormServiceTest {

    @Mock
    private OntologyHelper ontologyHelper;

    @InjectMocks
    private FormService qaService;

    @Test
    public void generateModuleForm() {
        //TODO consider how to test!
        Assert.assertEquals(1, 1);
    }


}