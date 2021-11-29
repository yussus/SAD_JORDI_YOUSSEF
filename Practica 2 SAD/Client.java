import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static final int PORT = 1234;
    public static final String HOST = "127.0.0.1";

    public static void main(String[] args) {

        MySocket mSocket = new MySocket(HOST, PORT);
        // INPUT
        new Thread(() -> {
            try {
                String linia;
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                while ((linia = input.readLine()) != null) {
                    mSocket.printLine(linia);
                }
                mSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

        // OUTPUT
        new Thread(() -> {
            String line;
            while ((line = mSocket.readLine()) != null) {
                System.out.println(line);
            }
        }).start();
    }
}