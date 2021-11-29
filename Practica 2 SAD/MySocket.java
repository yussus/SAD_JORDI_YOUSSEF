import java.io.*;
import java.net.Socket;

public class MySocket extends Socket {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public MySocket(String htName, int port) {
        try {
            this.socket = new Socket(htName, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException err) {
            System.err.println("Couldn't create the socket");
            err.printStackTrace();
        }
    }

    public MySocket(Socket socket) {
        try {
            this.socket = socket;
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            System.err.println("Couldn't create the socket");
            e.printStackTrace();
        }
    }

    public String readLine() {
        String str;
        try {
            str = input.readLine();
            return str;
        } catch (IOException err) {
            err.printStackTrace();
        }
        return null;
    }

    public void printLine(String str) {
        output.println(str);
        output.flush();
    }

    @Override
    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException err) {
            System.err.println("Couldn't close the socket");
        }
    }
}
