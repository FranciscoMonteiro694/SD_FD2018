
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import static java.lang.Thread.sleep;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloServer extends UnicastRemoteObject implements Hello_S_I {

    static public ArrayList<Hello_C_I> todos_clientes = new ArrayList<Hello_C_I>();
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    public MulticastSocket socket = null;
    public MulticastSocket socket2 = null;
    HashMap<Integer, Hello_C_I> queue;
    public int id;

    public HelloServer() throws RemoteException {
        id = 0;
        try {
            socket2 = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket2.joinGroup(group);
            socket = new MulticastSocket();
        } catch (IOException ex) {
            Logger.getLogger(HelloServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        queue = new HashMap<Integer, Hello_C_I>();

    }

    public void envia() {

    }

    public void print_on_server(String s) throws RemoteException {
        System.out.println("> " + s);
        //wait();
    }
//para apagar

    public void subscribe(String name, Hello_C_I c) throws RemoteException {
        System.out.println("Subscribing " + name);
        System.out.print("> ");
        todos_clientes.add(c);
    }

    public boolean login(String name, String password, Hello_C_I c) throws RemoteException {
        System.out.println("A dar login " + name);
        send(this.socket, this.MULTICAST_ADDRESS, this.PORT, "type|login;username|" + name + ";password|" + password + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1");
        String d = recive(this.socket2);
        d = recive(this.socket2);
        HashMap aux=String_To_Hash(d);
        d=HashToString(aux);
        c.print_on_client(d);
        if(aux.get("login_try").equals("sucess")){
            return true;
        }
        return false;
    }
    public void send_recive(String s){
        
    }
    public boolean register(String name, String password, Hello_C_I c) throws RemoteException {
        System.out.println("A registar " + name);
        send(this.socket, this.MULTICAST_ADDRESS, this.PORT, "type|register;username|" + name + ";password|" + password + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1");
        String d = recive(this.socket2);
        d = recive(this.socket2);
        HashMap aux=String_To_Hash(d);
        d=HashToString(aux);
        c.print_on_client(d);
        if(aux.get("regist_try").equals("sucess")){
            return true;
        }
        return false;
        /*synchronized(queue){
            queue.put(id,c);
            id++;
        }*/
    }
    public boolean is_Editor(String name,Hello_C_I c)throws RemoteException{
        System.out.println("A verificar se e editor " + name);
        send(this.socket, this.MULTICAST_ADDRESS, this.PORT, "type|request_permission_gerir;username|" + name  + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1");
        String d = recive(this.socket2);
        d = recive(this.socket2);
        HashMap aux=String_To_Hash(d);
        d=HashToString(aux);
        c.print_on_client(d);
        if(aux.get("acess").equals("granted")){
            return true;
        }
        return false;
    }
    public boolean insere_Artista(String name,String genero,String descricao,String data,String username ,Hello_C_I c)throws RemoteException{
        System.out.println("A inserir artista " + name);
        send(this.socket, this.MULTICAST_ADDRESS, this.PORT, "type|inserir_artista;username|" + username+";artista_name|"+name+";artista_genero|"+genero+";artista_data|"+data+";artista_descricao|"+descricao  + ";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1");
        String d = recive(this.socket2);
        d = recive(this.socket2);
        HashMap aux=String_To_Hash(d);
        d=HashToString(aux);
        c.print_on_client(d);
        if(aux.get("insere_artista_try").equals("sucess")){
            return true;
        }
        return false;
        
    }
    public boolean remove_Artista(String name,String username,Hello_C_I c)throws RemoteException{
        System.out.println("A remover artista " + name);
        send(this.socket, this.MULTICAST_ADDRESS, this.PORT, "type|remover_artista;username|" + username+";artista_name|"+name+";ID|" + Integer.toString(id) + ";request|true;" + "mserverid|1");
        String d = recive(this.socket2);
        d = recive(this.socket2);
        HashMap aux=String_To_Hash(d);
        d=HashToString(aux);
        c.print_on_client(d);
        if(aux.get("remove_artista_try").equals("sucess")){
            return true;
        }
        return false;
    }
    public static String HashToString(HashMap<String, String> s) {
            String r = "";
            s.remove("ID");
            s.remove("request");
            Set set = s.entrySet();
            Iterator iterator = set.iterator();
            //chave1|valor1;ID|1
            while (iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry) iterator.next();
                System.out.print("key is: " + mentry.getKey() + " & Value is: ");
                System.out.println(mentry.getValue());
                r = r + mentry.getKey() + "|" + mentry.getValue() + ";";
            }
            r = r.substring(0, r.length() - 1);
            return r;
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

    public void getQueue() {
        Set set = queue.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
        }

    }

    // =======================================================
    public static void main(String args[]) {
        String a;
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        try {
            //User user = new User();
            LocateRegistry.createRegistry(1099);
            HelloServer h = new HelloServer();
            Naming.rebind("XPTO", h);
            System.out.println("Hello Server ready.");
            //new Handler_Response(h.queue);
            //new RMI_Backup();

        } catch (Exception re) {
            System.out.println("Exception in HelloImpl.main: " + re);
        }
    }

    //ta mal
    public static void send(MulticastSocket socket, String MULTICAST_ADDRESS, int PORT, String s) {
        try {
            byte[] buffer = s.getBytes();
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 4321);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            socket.close();
        }*/

    }
    public void insere_Album(String name,String username,Hello_C_I c)throws RemoteException{
        
    }
    public void remove_Album(String name,String username,Hello_C_I c)throws RemoteException{
        
    }

    public static String recive(MulticastSocket socket) {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);

        } catch (IOException ex) {
            Logger.getLogger(HelloServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
        String message = new String(packet.getData(), 0, packet.getLength());
        HashMap<String, String> aux = String_To_Hash(message);
        System.out.println(message);
        if (aux.containsKey("request")) {
            System.out.println("Javardice");
        } else {
            System.out.println("Niceeee");
            return message;
        }
        return "";

    }
}

    class Handler_Response implements Runnable {

        public HashMap<Integer, Hello_C_I> queue;
        public MulticastSocket socket;
        private String MULTICAST_ADDRESS = "224.0.224.0";
        private int PORT = 4321;

        public Handler_Response(HashMap<Integer, Hello_C_I> queue) {
            this.queue = queue;
            try {
                socket = new MulticastSocket(PORT);  // create socket and bind it
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                socket.joinGroup(group);
                new Thread(this, "Handler_Response").start();
            } catch (Exception e) {
                System.out.println("uppssss");
            }

        }

        public void run() {
            System.out.println("Thread ta a correr e a escuta");

            while (true) {
                try {
                    System.out.println("--------------------");
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(message);
                    HashMap<String, String> aux;
                    aux = String_To_Hash(message);
                    if (aux.get("request").equals("true")) {
                        System.out.println("Javardice");
                    } else {
                        int id = Integer.parseInt(aux.get("ID"));
                        synchronized (queue) {
                            queue.get(id).print_on_client(message);
                            queue.remove(id);
                            Set set = queue.entrySet();
                            Iterator iterator = set.iterator();
                            //chave1|valor1;ID|1
                            System.out.println("Ta vazio");
                            while (iterator.hasNext()) {
                                Map.Entry mentry = (Map.Entry) iterator.next();
                                System.out.print("key is: " + mentry.getKey() + " & Value is: ");
                                System.out.println(mentry.getValue());
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    

        public static HashMap<String,String> String_To_Hash(String s) {

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

        public static String HashToString(HashMap<String, String> s) {
            String r = "";
            s.remove("ID");
            s.remove("request");
            Set set = s.entrySet();
            Iterator iterator = set.iterator();
            //chave1|valor1;ID|1
            while (iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry) iterator.next();
                System.out.print("key is: " + mentry.getKey() + " & Value is: ");
                System.out.println(mentry.getValue());
                r = r + mentry.getKey() + "|" + mentry.getValue() + ";";
            }
            r = r.substring(0, r.length() - 1);
            return r;
        }

    }
