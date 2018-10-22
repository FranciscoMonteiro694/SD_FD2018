import java.io.Serializable;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.lang.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MulticastServer extends Thread implements Serializable {
    private static int server_id;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT_REC = 4321;
    private ArrayList<User> users;//Lista de utilizadores
    private ArrayList<Musica> musicas;//Lista de musicas
    private ArrayList<Artista> artistas;//Lista de artistas, (um artista pode ser um grupo)
    private ArrayList<Album> albuns;//Lista de albuns
    private ArrayList<Notificacao> notificacoes;//Lista de notificacoes


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
        users=new ArrayList<>();
        musicas=new ArrayList<>();
        notificacoes=new ArrayList<>();
        musicas=new ArrayList<>();
        albuns=new ArrayList<>();
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
                if(map.containsKey("mserverid")) {
                    id = Integer.parseInt(map.get("mserverid"));
                    if (id == server_id) {
                        Worker thread = new Worker(map, socket, users,server_id,musicas,artistas,albuns,notificacoes);
                        thread.start();
                    }
                    else{//tratar das cenas de registers e assim, talvez fazer um switch aqui dentro

                    }
                }
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
            r.put(parts2[0], parts2[1]); //esta a dar erro aqui
        }
        return r;

    }

}
//Vou ter de pegar no pacote que receber, vou ao protocolo e vou buscar o id do servidor que vai responder
class Worker extends Thread{
    private int server_id;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private ArrayList<User> users;//Lista de utilizadores
    private ArrayList<Musica> musicas;//Lista de musicas
    private ArrayList<Artista> artistas;//Lista de artistas, (um artista pode ser um grupo)
    private ArrayList<Album> albuns;//Lista de albuns
    private ArrayList<Notificacao> notificacoes;//Lista de notificacoes

    private HashMap<String,String> mensagem;

    private MulticastSocket socket;

    Worker(HashMap<String,String> mensagem,MulticastSocket socket,ArrayList<User> users,int server_id, ArrayList<Musica> musicas,ArrayList<Artista> artistas,ArrayList<Album> albuns,ArrayList<Notificacao> notificacoes){//recebe a mensagem como pedido
        this.mensagem=mensagem;
        this.socket=socket;
        this.users=users;
        this.notificacoes=notificacoes;
        this.albuns=albuns;
        this.artistas=artistas;
        this.musicas=musicas;
        this.server_id=server_id;
    }

    public void run(){
        String aux;
        System.out.println("Thread para tratar do pedido criada!");
        //Tratar da resposta
        switch(mensagem.get("type")){//é preciso dar handle do quit?
            case "login"://para dar login
                String[] logins_bd=new String[2];//para guardar os logins em causa da base de dados e comparar com os da mensagem
                //Vou pegar no username e na password da mensagem e vou ver se está na base de dados
                for(User u : users){
                    if(u.getUsername().equals(mensagem.get("username"))){
                        logins_bd[0]=u.getUsername();
                        logins_bd[1]=u.getPassword();
                    }
                }
                //Se estiver, responder com uma mensagem a dizer que foi logado com sucesso
                if(logins_bd[0].equals(mensagem.get("username")) && logins_bd[1].equals(mensagem.get("password"))){
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String mensagem = "login_try|sucess";
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
                //Caso contrário, enviar mensagem a dizer que está incorrecto
                else{
                    //Mandar mensagem "Login incorrecto!"
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String mensagem = "login_try|failed";
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
                break;
            case "register"://para registar, tambem vai ter de ser feito nos outros servidores
                //verificar se o nome já está na base de dados
                //se estiver
                if(users.isEmpty()==true){
                    register_admin(mensagem.get("username"),mensagem.get("password"));
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String mensagem = "regist_try|sucess;msg|Foi registado como admin!;";
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    for (User u : users) {//Está a dar null pointer exception
                        if (u.getUsername().equals(mensagem.get("username"))) {//se existir, enviar mensagem a dizer que falhou
                            try {
                                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                                String mensagem = "regist_try|failed";
                                byte[] buffer = mensagem.getBytes();
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socket.send(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {//se for bem sucedido, registar e adicionar à base de dados
                            register(mensagem.get("username"), mensagem.get("password"));
                            try {
                                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                                String mensagem = "regist_try|sucess";
                                byte[] buffer = mensagem.getBytes();
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socket.send(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
                break;
            case "write_review"://para escrever criticas
                //vai ter de enviar a lista de todos os albuns para o utilizador escolher qual quer
                criticar_album();
                break;
            case "write_review2":
                criticar_album2();
                break;
            case "make_editor"://verificar se o utilizador em causa é o admin ou editor
                make_editor();
                break;
            case "view_notificacoes":
                ver_notificacoes();
                break;


        }
    }
    //Quando um cliente dá login, vai enviar uma mensagem para o servidor para ele ver se tem notificacoes
    public void ver_notificacoes(){
        int counter=0;
        //Procurar em todas as notificacoes se há alguma com o nome do username em causa
        for(Notificacao n:notificacoes){
            if (n.getDestinario().equals(mensagem.get("username"))){//se tiver notificacoes para este utilizador
                try {
                    InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                    String aux=mensagem.get("username")+";notification|"+n.getNota();
                    counter++;
                    String mensagem = "username|"+aux;
                    byte[] buffer = mensagem.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                    //Remover a notifcacao da array List para não voltar a repetir posteriormente
                    notificacoes.remove(n);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        //Se não tiver notificações
        if (counter==0){
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux=mensagem.get("username")+";msg|Nao tem notificacoes!";
                String mensagem = "username|"+aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    //Metodo que vai dar permissoes a outros utilizadores
    public void make_editor(){
        String aux;
        //Vai verificar se o user em questao tem permissao de admin ou utilizador
        //Nao tem permissao, enviar mensagem
        if(mensagem.get("user_type").equals("normal")){
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String mensagem = "acess|denied;msg|Nao tem permissoes";
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        if(mensagem.get("user_type").equals("admin") || mensagem.get("user_type").equals("editor")){
            //Ver a que utilizador quer dar permissoes
            for(User u:users){
                if(u.getUsername().equals(mensagem.get("editor_name"))){//se encontrou o nome
                    if(u.getUsertype().equals("editor") || u.getUsertype().equals("admin")){//se já tem permissoes
                        try {
                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                            String mensagem = "msg|O utilizador já tem permissões!";
                            byte[] buffer = mensagem.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                            socket.send(packet);
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                    else{//se ainda não tem permissões
                        try {
                            u.setUsertype("editor");//altera as permissoes
                            aux="editor_made|"+mensagem.get("username");
                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                            String mensagem = "msg|Permissoes atualizadas!;"+aux;
                            byte[] buffer = mensagem.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                            socket.send(packet);
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
                else{//se não encontrou o nome
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String mensagem = "msg|O utilizador não existe!";
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //Função que vai enviar por mensagem a lista de todos os albuns
    //Falta testar
    public void criticar_album(){
        int tamanho=albuns.size();
        //Se não houver album nenhum
        if(tamanho==0){
            //Enviar mensagem a dizer que não há albuns
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String mensagem = "type|warning;msg|Não há albuns disponveis!!";
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
            catch(IOException e){
                e.printStackTrace();
            }

        }
        else {
            String mensagem = "type|album_list;item_count|" + Integer.toString(tamanho) + ";";
            for (int i = 0; i < albuns.size(); i++) {
                mensagem = mensagem + "item_" + Integer.toString(i) + "_name|" + albuns.get(i).getNome() + ";";
            }
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    void criticar_album2(){
        //Vai receber o album que o utilizador quer criticar, a sua mensagem e a cotacao
        ArrayList<Critica> criticas;
        for(Album a: albuns){
            if(a.getNome().equals(mensagem.get("item_0_name"))){//Quando encontra o album na lista de albuns
                Critica c = new Critica (mensagem.get("review_txt"),Integer.parseInt(mensagem.get("review_avaliacao")));
                criticas=a.getCriticas();
                criticas.add(c);
                a.setCriticas(criticas);
            }
        }
        try {
            String mensagem="type|warning;msg|Critica escrita com sucesso!";
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            byte[] buffer = mensagem.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    //Função para registar o admin, só vai ser utilizada uma vez
    void register_admin(String username,String password){
        User novo;
        novo = new User(username,password,"admin");
        users.add(novo);
    }
    //Funcao para registar pessoa
    void register(String username,String password){
        User novo;
        novo = new User(username, password,"normal");
        //adiciona ao array list de utilizadores
        users.add(novo);
    }
    /* Funções para lidar com ficheiros */
    /* Falta testar */

    void leObjetosArtistas(){
        ObjectInputStream ois=null;
        try{
            String filename="artistas"+Integer.toString(server_id)+".obj";
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
        }catch(IOException e) {
            System.out.println(" ");
        }
        if(ois!=null){
            try {
                artistas = (ArrayList<Artista>) ois.readObject();
                ois.close();
            }catch(ClassNotFoundException e){
                System.out.println(e);
            }catch(IOException e) {
                System.out.println(e);
            }
        }
    }
    void leObjetosMusicas(){
        ObjectInputStream ois=null;
        try{
            String filename="musicas"+Integer.toString(server_id)+".obj";
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
        }catch(IOException e) {
            System.out.println(" ");
        }
        if(ois!=null){
            try {
                musicas = (ArrayList<Musica>) ois.readObject();
                ois.close();
            }catch(ClassNotFoundException e){
                System.out.println(e);
            }catch(IOException e) {
                System.out.println(e);
            }
        }
    }
    void leObjetosNotificacoes(){
        ObjectInputStream ois=null;
        try{
            String filename="notificacoes"+Integer.toString(server_id)+".obj";
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
        }catch(IOException e) {
            System.out.println(" ");
        }
        if(ois!=null){
            try {
                notificacoes = (ArrayList<Notificacao>) ois.readObject();
                ois.close();
            }catch(ClassNotFoundException e){
                System.out.println(e);
            }catch(IOException e) {
                System.out.println(e);
            }
        }
    }
    void leObjetosUtilizadores(){
        ObjectInputStream ois=null;
        try{
            String filename="utilizadores"+Integer.toString(server_id)+".obj";
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
        }catch(IOException e) {
            System.out.println(" ");
        }
        if(ois!=null){
            try {
                users = (ArrayList<User>) ois.readObject();
                ois.close();
            }catch(ClassNotFoundException e){
                System.out.println(e);
            }catch(IOException e) {
                System.out.println(e);
            }
        }
    }
    public void guardarUtilizadores(ArrayList<User> u){
        try{
            String filename="utilizadores"+Integer.toString(server_id)+".obj";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(u);
            os.close();
        }catch(IOException e){
            System.out.printf("Ocorreu a exceçao %s ao escrever no ficheiro de objetos dos utilizadores.\n", e);
        }
    }
    public void guardarArtistas(ArrayList<Artista> a){
        try{
            String filename="artistas"+Integer.toString(server_id)+".obj";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(a);
            os.close();
        }catch(IOException e){
            System.out.printf("Ocorreu a exceçao %s ao escrever no ficheiro de objetos dos artistas.\n", e);
        }
    }
    public void guardarNotificacoes(ArrayList<Notificacao> n){
        try{
            String filename="notificacoes"+Integer.toString(server_id)+".obj";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(n);
            os.close();
        }catch(IOException e){
            System.out.printf("Ocorreu a exceçao %s ao escrever no ficheiro de objetos das notificacoes.\n", e);
        }
    }
    public void guardarMusicas(ArrayList<Musica> m){
        try{
            String filename="musicas"+Integer.toString(server_id)+".obj";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(m);
            os.close();
        }catch(IOException e){
            System.out.printf("Ocorreu a exceçao %s ao escrever no ficheiro de objetos dos musicas.\n", e);
        }
    }
}


