import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.StringTokenizer;

import static spark.Spark.*;

@WebSocket
public class TritonCodeServer {
    static ServerDriver serverDriver;

    public static void main(String[] args) {
        System.out.println("Hello, World!");

        port(3000);
        staticFileLocation("/public");
        //webSocket("/chat", TritonCodeServer.class);
        webSocket("/code", TritonCodeServer.class);
        init();
    }

    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + OperationSender.nextUserNumber++;
        OperationSender.userUsernameMap.put(user, username);
        //OperationSender.broadcastMessage(sender = "Server", msg = (username + " joined the chat"));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = OperationSender.userUsernameMap.get(user);
        OperationSender.userUsernameMap.remove(user);
        //OperationSender.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        System.out.println(message);
        if (message.contains("START:")) {
            int newlinePos = message.indexOf((char) 0);
            String key = message.substring(6, newlinePos);
            String document = message.substring(newlinePos + 1);
            serverDriver = new ServerDriver(document, key);
            System.out.println(serverDriver.getDocument().getData());
        } else if (message.contains("DOCUMENT")) {
            // TODO: check if file exists
            StringTokenizer tokenizer = new StringTokenizer(message, "" + (char) 0);
            tokenizer.nextElement();
            String key = (String) tokenizer.nextElement();
            String editKey = (String) tokenizer.nextElement();
            String parentKey = (String) tokenizer.nextElement();
            String edits = (String) tokenizer.nextElement();
            serverDriver.enqueueClientOperation(new ServerOperation(OperationParser.strToOperation(edits), editKey, parentKey));
            serverDriver.processChange();
            System.out.println(serverDriver.getDocument().getData());

            // Broadcast server message
            ServerOperation serverOperation = serverDriver.sendServerOperationToClient();
            OperationSender.broadcastOperation(sender = "Server", msg = "DOCUMENT" + (char) 0 + serverDriver.getKey() + (char) 0 + serverOperation.getKey() + (char) 0 + serverOperation.getParentKey() + (char) 0 + OperationParser.operationToStr(serverOperation.getOperation()));
        } else if (message.contains("CONNECT")) {
            StringTokenizer tokenizer = new StringTokenizer(message, "" + (char) 0);
            tokenizer.nextElement();
            String key = (String) tokenizer.nextElement();

            try {
                user.getRemote().sendString("START:" + key + (char) 0 + serverDriver.getDocument().getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
