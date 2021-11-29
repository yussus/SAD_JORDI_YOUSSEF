import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Runnable {
    public static ConcurrentHashMap<String, MySocket> clients = new ConcurrentHashMap<>();
    public static boolean st = true;
    public MySocket cSocket;
    public String user;

    public Server(String user, MySocket sckt) {
        this.cSocket = sckt;
        this.user = user;
    }

    public static void main(String[] args) {
        MyServerSocket server = new MyServerSocket(1234);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        System.out.println(dateFormat.format(new Date()) + " The server is running");

        while (true) {
            MySocket nclnt = server.accept();
            st = true;
            while (st) {
                nclnt.printLine("Introduce your nick: ");
                String user = nclnt.readLine();
                if (clients.containsKey(user)) {
                    nclnt.printLine(dateFormat.format(new Date()) + " The nick " + user + " already exists");
                } else {
                    System.out.println(dateFormat.format(new Date()) + " Welcome " + user);
                    clients.put(user, nclnt);
                    new Thread(new Server(user, nclnt)).start();
                    st = false;
                }
            }
            st = true;
        }
    }

    @Override
    public void run() {
        String linia;
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        while ((linia = clients.get(user).readLine()) != null) {
            for (ConcurrentHashMap.Entry<String, MySocket> entry : clients.entrySet()) {
                if (!entry.getKey().equals(user)) {
                    entry.getValue().printLine(dateFormat.format(new Date()) + " [" + user + "]: " + linia);
                }
            }
        }
        clients.get(user).close();
        clients.remove(user);
        System.out.println(dateFormat.format(new Date()) + " " + user + " has left.");
    }
}