import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.lang.*;

public class MulticastServer extends Thread {
    private static int server_id;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT_REC = 4321; // Porto para receber
    private int PORT_ENV = 4322; // Porto para enviar

    public static void main(String[] args) { //meter um nº de servidor
        String teste = args[0];
        server_id = Integer.parseInt(teste);
        MulticastServer server = new MulticastServer();
        server.start();
        
        System.out.println("Servidor com nº : "+ server_id);

    }

    public MulticastServer() {
        super("Server running " + server_id);
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName());

        try {
            socket = new MulticastSocket(PORT_REC);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while(true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}


