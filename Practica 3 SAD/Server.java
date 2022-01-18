import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

	public static void main(String[] args) {
		ServerFrame sframe = new ServerFrame();
		sframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

class ServerFrame extends JFrame implements Runnable {

	private JTextArea espaitext;

	public ServerFrame() {
		setBounds(1200, 300, 280, 350);
		JPanel stemplate = new JPanel();
		stemplate.setLayout(new BorderLayout());
		espaitext = new JTextArea();
		stemplate.add(espaitext, BorderLayout.CENTER);
		add(stemplate);
		setVisible(true);
		Thread sthread = new Thread(this);
		sthread.start();
	}

	@Override
	public void run() { // para que la clase este continuamente escuchando
		try {
			ServerSocket servidor = new ServerSocket(9999);
			String nick, ip, mensaje;
			HashMap<String, String> IpNombre = new HashMap<String, String>();
			Contenedor paquete_recibido;
			while (true) {
				Socket ssocket = servidor.accept(); // le decimos que acepte las conexiones
				ObjectInputStream paquete_datos = new ObjectInputStream(ssocket.getInputStream());
				paquete_recibido = (Contenedor) paquete_datos.readObject();
				nick = paquete_recibido.getNick();
				ip = paquete_recibido.getIp();
				mensaje = paquete_recibido.getMensaje();

				if (!mensaje.equals("connect")) {
					espaitext.append("\n" + nick + ": " + mensaje + " para " + ip);
					// Reenvio del paquete del server a los clientes
					Socket enviaDestinatario = new Socket(ip, 9090);
					ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
					paqueteReenvio.writeObject(paquete_recibido);
					paqueteReenvio.close();
					enviaDestinatario.close();
					ssocket.close();
				} else {
					InetAddress localizacion = ssocket.getInetAddress();
					String IpRemota = localizacion.getHostAddress();
					IpNombre.put(nick, IpRemota);
					paquete_recibido.setIpnombres(IpNombre);
					for (String str : IpNombre.values()) {
						Socket enviaDestinatario = new Socket(str, 9090);
						ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
						paqueteReenvio.writeObject(paquete_recibido);
						paqueteReenvio.close();
						enviaDestinatario.close();
						ssocket.close();
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}