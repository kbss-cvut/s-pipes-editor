package og_spipes.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public abstract class AbstractControllerTest extends AbstractSpringTest {

    @Autowired
    protected MockMvc mockMvc;
}
