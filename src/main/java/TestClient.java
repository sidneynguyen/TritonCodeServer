import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class TestClient {
    Session userSession = null;
    private MessageHandler messageHandler;

    public TestClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider
                    .getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null)
            this.messageHandler.handleMessage(message);
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public static interface MessageHandler {
        public void handleMessage(String message);
    }

    public static void main(String[] args) throws Exception {
        final TestClient testClient = new TestClient(new URI("ws://localhost:3000/code"));
        testClient.addMessageHandler(new MessageHandler() {
            @Override
            public void handleMessage(String message) {
                System.out.println(message);
            }
        });

        testClient.sendMessage("START:1234\nabc");
        //Thread.sleep(1000);
        testClient.sendMessage("DOCUMENT\n1234\n4321\n1234\nIx,R3,\n");
        while (true) {
            Thread.sleep(1000);
        }
    }
}
