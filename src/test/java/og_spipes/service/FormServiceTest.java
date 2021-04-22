package og_spipes.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class FormServiceTest {

    @Mock
    private OntologyHelper ontologyHelper;

    @InjectMocks
    private FormService qaService;

    @Test
    public void generateModuleForm() {
        //TODO consider how to test!
        Assertions.assertEquals(1, 1);
    }


}