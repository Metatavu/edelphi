package fi.metatavu.edelphi.query;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.io.IOUtils;

@ServerEndpoint ("/ws/socket/{role}")
public class AnswerWebSocket {
  
  private static final Logger logger = Logger.getLogger(AnswerWebSocket.class.getName());
  
  private static Map<String, Session> liveSessions = Collections.synchronizedMap(new HashMap<>());
  
  @OnOpen
  public void onOpen(final Session session, EndpointConfig endpointConfig, @PathParam ("role") String role) {
    if ("live".equals(role)) {
      liveSessions.put(session.getId(), session);
    }
  }
  
  @OnClose
  public void onClose(final Session session, CloseReason closeReason) {
    liveSessions.remove(session.getId());
  }

  @OnMessage
  public void onMessage(Reader messageReader, Session session) {
    String message = null;
    try {
      message = IOUtils.toString(messageReader);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to read message", e);
    }
    
    for (Entry<String, Session> sessionEntry : liveSessions.entrySet()) {
      if (!sessionEntry.getKey().equals(session.getId())) {
        sessionEntry.getValue()
          .getAsyncRemote()
          .sendText(message); 
      }
    }
    
  }
  
  
}