import java.io.IOException;
import java.net.ServerSocket;

public class MyServerSocket {
    ServerSocket sSocket;

    public MyServerSocket(int port) {
        try {
            sSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MySocket accept() {
        MySocket cs;
        try {
            cs = new MySocket(this.sSocket.accept());
            return cs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
