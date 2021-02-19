package og_spipes.rest;

import org.apache.commons.io.FileUtils;
import org.java_websocket.WebSocket;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application.properties")
public class NotificationControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationControllerTest.class);

    @Value("${local.server.port}")
    private int port;

    private String URL;

    @Value("${repositoryUrl}")
    private String repositoryUrl;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${spring.mvc.servlet.path}")
    private String servletPath;


    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(repositoryUrl);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
        URL = "ws://localhost:" + port + contextPath  + servletPath + "notifications";
    }

    @Test
    void testMonitoringOfRootFSAndNotificationService() throws Exception{
        MyWebSocketClient myWebSocketClient = new MyWebSocketClient(new URI(URL));
        myWebSocketClient.connect();
        while (!WebSocket.READYSTATE.OPEN.equals(myWebSocketClient.getReadyState())){
            LOG.info("WebSocket Client connection, please wait...");
            Thread.sleep(500);
        }

        myWebSocketClient.send(repositoryUrl + "/sample-script.ttl");
        Thread.sleep(1000);

        File file = new File(repositoryUrl + "/sample-script.ttl");
        String fileContext = FileUtils.readFileToString(file, "UTF-8");
        fileContext = fileContext + "\nnew_line";
        FileUtils.write(file, fileContext, "UTF-8");
        Thread.sleep(2000);

        file.delete();
        Thread.sleep(2000);

        Assert.assertEquals(2, myWebSocketClient.getOnMessageCounter().get());
        myWebSocketClient.close();
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(repositoryUrl));
    }

}