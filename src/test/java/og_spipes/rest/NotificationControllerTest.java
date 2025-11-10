package og_spipes.rest;

import og_spipes.config.Constants;
import og_spipes.testutil.AbstractSpringTest;
import org.apache.commons.io.FileUtils;
import org.awaitility.Awaitility;
import org.java_websocket.enums.ReadyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.net.URI;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationControllerTest extends AbstractSpringTest {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationControllerTest.class);

    @Value("${local.server.port}")
    private int port;

    private String URL;

    @Value(Constants.SCRIPTPATH_SPEL)
    private String scriptPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${spring.mvc.servlet.path}")
    private String servletPath;

    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(scriptPath);
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
        URL = "ws://localhost:" + port + contextPath  + servletPath + "notifications";
    }

    @Test
    void testMonitoringOfRootFSAndNotificationService() throws Exception{
        MyWebSocketClient myWebSocketClient = new MyWebSocketClient(new URI(URL));
        myWebSocketClient.connect();
        while (!ReadyState.OPEN.equals(myWebSocketClient.getReadyState())){
            LOG.info("WebSocket Client connection, please wait...");
            Thread.sleep(500);
        }

        File file = new File(scriptPath + "/sample-script.ttl");
        myWebSocketClient.send(file.toURI().getPath());
        Thread.sleep(1000);

        String fileContext = FileUtils.readFileToString(file, "UTF-8");
        fileContext = fileContext + "\nnew_line";
        FileUtils.write(file, fileContext, "UTF-8");
        Thread.sleep(2000);

        file.delete();
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .until(() -> myWebSocketClient.getOnMessageCounter().get() > 1);

        myWebSocketClient.close();
    }

}