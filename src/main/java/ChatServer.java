import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private final List<ChatMessage> message = new ArrayList();
    private static final Logger LOG = LoggerFactory.getLogger(ChatServer.class);
    private final int port  ;

    public ChatServer(final int port) {

        //validacion del puerto
        if (port < 1024 || port > 65535) throw new IllegalArgumentException(" porfavor use un puerto correcto");
        else this.port = port;

    }

    public void add(final ChatMessage chatMessage) {
        if (chatMessage == null) {
            throw new IllegalArgumentException(" no se pudo incertar el memsage ");
        } else {
            this.message.add(chatMessage);
        }
    }

    private List<ChatMessage> get() {
        return this.message;
    }

    public void start() throws IOException  {
        LOG.debug("comenzando el servicio por el puerto : {}",this.port);
        }
        //final String args[]
    public static void main(){

    }

}
