import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.nio.ByteBuffer;

public class ChatServer {
    private PingMessage message;
    private SessionConnectEvent connectEvent;

    public ChatServer() {
        message = new PingMessage(ByteBuffer.wrap("This is the message.".getBytes()));
        //connectEvent = new SessionConnectEvent()
    }
}