import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.lang.*;
import java.util.HashMap;

public class MulticastServer extends Thread {
    private static int server_id;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT_REC = 4321; // Porto para receber, posso ter o mesmo porto para enviar e receber
    private int PORT_ENV = 4322; // Porto para enviar
    public String delimitador=";|";

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
        HashMap<String,String> map;

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
                map=String_To_Hash(message);
                int id;
                id=Integer.parseInt(map.get("mserverid"));
                if (id==server_id){
                    Worker thread = new Worker(map);
                    thread.start();
                }
                //se não for, não faz nada
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }


    public static HashMap<String, String> String_To_Hash(String s) {
        HashMap<String, String> r = new HashMap<String, String>();
        String[] parts = s.split(";");
        //chave1|valor1;chave2|valor dois
        String[] parts2;
        for (int i = 0; i < parts.length; i++) {
            parts2 = parts[i].split("\\|");
            r.put(parts2[0], parts2[1]);
        }
        return r;

    }
}
//Vou ter de pegar no pacote que receber, vou ao protocolo e vou buscar o id do servidor que vai responder
class Worker extends Thread{
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    HashMap<String,String> mensagem;

    Worker(HashMap<String,String> mensagem){//recebe a mensagem como pedido

        this.mensagem=mensagem;
    }

    public void run(){
        System.out.println("Thread para tratar do pedido criada!");
        //Tratar da resposta
        switch(mensagem.get("type")){//é preciso dar handle do quit?
            case "login"://para dar login
                //Vou pegar no username e na password da mensagem e vou ver se está na base de dados
                String[] logins_bd=new String[2];//para guardar os logins em causa da base de dados e comparar com os da mensagem
                //Se estiver, responder com uma mensagem a dizer que foi logado com sucesso
                if(logins_bd[0].equals(mensagem.get("login")) && logins_bd[1].equals(mensagem.get("password"))){
                    //Mandar mensagem "Login feito com sucesso!"
                    String mensagem="Login feito com sucesso!";
                    byte[] buffer = mensagem.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);

                }
                //Caso contrário, enviar mensagem a dizer que está incorrecto
                else{
                    //Mandar mensagem "Login incorrecto!"
                    String mensagem="Login incorrecto!";
                    byte[] buffer = mensagem.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                }
                break;
        }
    }
}


