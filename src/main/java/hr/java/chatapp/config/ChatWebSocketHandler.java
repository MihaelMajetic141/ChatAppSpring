package hr.java.chatapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.java.chatapp.model.Message;
import hr.java.chatapp.service.MessageService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    // objectMapper.registerModule(new JavaTimeModule());

    @Autowired
    private MessageService messageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket connection established, session id: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        System.out.println("Received message payload: " + payload);
        Message incomingMessage = objectMapper.readValue(payload, Message.class);

        Message savedMessage = messageService.saveMessage(
                incomingMessage.getSenderId(),
                incomingMessage.getConversationId(),
                incomingMessage.getContent(),
                incomingMessage.getReplyTo()
        );

        String responseJson = objectMapper.writeValueAsString(savedMessage);
        session.sendMessage(new TextMessage(responseJson));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Transport error in session " + session.getId() + ": " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("WebSocket session " + session.getId() + " closed. Status: " + closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
