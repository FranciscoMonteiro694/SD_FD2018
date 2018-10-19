import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.lang.*;
import java.util.Random;

public class MulticastServer extends Thread {
    private static int server_id;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT_REC = 4321; // Porto para receber, posso ter o mesmo porto para enviar e receber
    private int PORT_ENV = 4322; // Porto para enviar

    public static void main(String[] args) { //meter um id do servidor
        String teste = args[0];
        server_id = Integer.parseInt(teste);
        MulticastServer server = new MulticastServer();
        server.start();
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
                //se o id do server for igual ao id do server que vem no pacote, criar nova thread e responder ao pedido

                //se não for, não faz nada
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
//Vou ter de pegar no pacote que receber, vou ao protocolo e vou buscar o id do servidor que vai responder
class Worker extends Thread{


    Worker(){

    }
}


