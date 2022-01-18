import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {

	public static void main(String[] args) {
		ClientFrame cframe = new ClientFrame();
		cframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

class ClientFrame extends JFrame {

	public ClientFrame() {
		TemplateClientFrame ctemplate = new TemplateClientFrame();
		setBounds(600, 300, 280, 350);
		add(ctemplate);
		setVisible(true);
		String nick = ctemplate.obtenirNick();
		addWindowListener(new Switching(nick));
	}

}

class Switching extends WindowAdapter { // con WindowListener tendriamos que sobreescribir todos sus metodos

	public String nick;

	public Switching(String nick) {
		this.nick = nick;
	}

	public void windowOpened(WindowEvent e) {
		try {
			Socket csocket = new Socket("192.168.214.15", 9999); // puerto 9999 es el del servidor que esta a la
																	// escucha
			Contenedor datos = new Contenedor();
			datos.setMensaje("connect");
			datos.setNick(nick);
			ObjectOutputStream paquete_datos = new ObjectOutputStream(csocket.getOutputStream());
			paquete_datos.writeObject(datos);
			csocket.close();
		} catch (Exception e2) {
			System.out.println("\n WINDOW OPENED ERROR");
		}
	}
}

class TemplateClientFrame extends JPanel implements Runnable {

	public TemplateClientFrame() {
		String nick_usuario = JOptionPane.showInputDialog("Nick: "); // para cuando abrimos el programa
		JLabel n_nick = new JLabel("Nick:"); // delante del nick
		add(n_nick);
		nick = new JLabel();
		nick.setText(nick_usuario);
		add(nick);
		JLabel texto = new JLabel("connect: ");
		add(texto);
		ip = new JComboBox<>();
		add(ip);
		espaitext = new JTextArea(12, 20);
		scroll = new JScrollPane(espaitext, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll);
		espaitext.setEditable(false);
		espai = new JTextField(20);
		add(espai);
		boto = new JButton("Enviar");
		EnviaTexto mievento = new EnviaTexto();
		boto.addActionListener(mievento);
		add(boto);
		Thread cthread = new Thread(this);
		cthread.start();
	}

	public String obtenirNick() {
		String Nick = nick.getText();
		return Nick;
	}

	private class EnviaTexto implements ActionListener {
		public void actionPerformed(ActionEvent e) { // cuando apretamos el botón de enviar
			espaitext.append("Yo: " + espai.getText() + "\n");
			try {
				Socket csocket = new Socket("192.168.214.15", 9999);

				// Como enviamos tres Strings(nick,ip,mensaje) enviaremos un objeto
				Contenedor datos = new Contenedor();
				datos.setNick(nick.getText());
				for (String str : mapa.keySet()) {
					if (ip.getSelectedItem().toString() == str) {
						ip_a_enviar = mapa.get(str);
					}
				}
				datos.setIp(ip_a_enviar);
				datos.setMensaje(espai.getText());
				ObjectOutputStream paquete_datos = new ObjectOutputStream(csocket.getOutputStream());
				paquete_datos.writeObject(datos);
				csocket.close();
				espai.setText("");
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
			}
		}
	}

	@Override
	public void run() {
		try {
			ServerSocket servidor_cliente = new ServerSocket(9090);
			Socket cliente;
			Contenedor paquete_Recibido;
			mapa = new HashMap<String, String>();
			while (true) {
				cliente = servidor_cliente.accept();
				ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
				paquete_Recibido = (Contenedor) flujoentrada.readObject();
				if (!paquete_Recibido.getMensaje().equals("connect")) {
					espaitext.append(paquete_Recibido.getNick() + ": " + paquete_Recibido.getMensaje() + "\n");
				} else {
					HashMap<String, String> NombresMenu = new HashMap<String, String>();
					NombresMenu = paquete_Recibido.getIpnombres();
					mapa.clear();
					mapa.putAll(NombresMenu);
					ip.removeAllItems();
					for (String str : NombresMenu.keySet()) {
						ip.addItem(str);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String ip_a_enviar;
	private JComboBox<String> ip;
	private JTextField espai;
	private JTextArea espaitext;
	private JLabel nick;
	private JButton boto;
	private JScrollPane scroll;
	private HashMap<String, String> mapa;

}

@SuppressWarnings("serial")
class Contenedor implements Serializable { // Serializable: para que todas las instancias que pertenezcan a esta clase
											// sean capaces de covertirse en una serie de bytes para poder ser enviadas
											// a traves de la red
	private String nick, ip, mensaje;
	private ArrayList<String> Ips;
	private HashMap<String, String> ipnombres;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public void setIpnombres(HashMap<String, String> ipnombres) {
		this.ipnombres = ipnombres;
	}

	public HashMap<String, String> getIpnombres() {
		return ipnombres;
	}
}