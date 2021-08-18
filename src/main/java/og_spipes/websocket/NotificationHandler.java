package og_spipes.websocket;


import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@ServerEndpoint(value = "/notifications")
public class NotificationHandler extends TextWebSocketHandler implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationHandler.class);
    private static final Map<String, Set<WebSocketSession>> fileSubscribers = Collections.synchronizedMap(new HashMap<>());

    @Value("${scriptPaths}")
    private String[] scriptPaths;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        LOG.info("Session closed " + session.toString());
        fileSubscribers.forEach((k, v) -> {
            fileSubscribers.get(k).remove(session);
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        String fileAbsPath = textMessage.getPayload();
        if(!fileAbsPath.equals("")){
            LOG.info("File added " + fileAbsPath);
            if(fileSubscribers.containsKey(fileAbsPath)){
                fileSubscribers.get(fileAbsPath).add(session);
            }else{
                Set<WebSocketSession> sessions = Stream.of(session).collect(Collectors.toSet());
                fileSubscribers.put(fileAbsPath, sessions);
            }
        }
    }

    private void notify(String file, String message) {
        if(fileSubscribers.containsKey(file)){
            fileSubscribers.get(file).forEach(session -> {
                try {
                    LOG.info("File has changed: " + file + ", sending message: " + message + ", to: " + session.toString());
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    LOG.error(e.getLocalizedMessage());
                }
            });
        }
    }

    //TODO write to thesis about simplification
    @Override
    public void afterPropertiesSet() throws Exception {
        for(String folderToObserve : scriptPaths){
            LOG.info("Monitoring folder: " + folderToObserve);
            FileAlterationObserver observer = new FileAlterationObserver(folderToObserve);
            FileAlterationMonitor monitor = new FileAlterationMonitor(3000);
            FileAlterationListener listener = new FileAlterationListenerAdaptor() {
                @Override
                public void onFileCreate(File file) {
                    LOG.info("File created: " + file.getAbsolutePath());
                    NotificationHandler.this.notify(file.getAbsolutePath(), "File created");
                }

                @Override
                public void onFileDelete(File file) {
                    LOG.info("File deleted: " + file.getAbsolutePath());
                    NotificationHandler.this.notify(file.getAbsolutePath(), "File deleted");
                }

                @Override
                public void onFileChange(File file) {
                    LOG.info("File changed: " + file.getAbsolutePath());
                    NotificationHandler.this.notify(file.getAbsolutePath(), "File changed");
                }
            };
            observer.addListener(listener);
            monitor.addObserver(observer);
            monitor.start();

        }
    }

    //TODO use later for tests -
    // https://medium.com/@MelvinBlokhuijzen/spring-websocket-endpoints-integration-testing-180357b4f24c
    // https://programmer.help/blogs/spring-boot-development-series-experience-in-developing-websocket.html

}
