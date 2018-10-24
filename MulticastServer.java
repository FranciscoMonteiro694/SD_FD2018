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
    private int PORT = 4321;
    private ArrayList<User> users;//Lista de utilizadores
    private ArrayList<Musica> musicas;//Lista de musicas
    private ArrayList<Artista> artistas;//Lista de artistas, (um artista pode ser um grupo)
    private ArrayList<Album> albuns;//Lista de albuns
    private ArrayList<Notificacao> notificacoes;//Lista de notificacoes


    public static void main(String[] args) { //meter um id do servidor
        String teste = "1";
        server_id = Integer.parseInt(teste);
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer() {
        super("Server running " + server_id);
    }

    public void run() {
        users = new ArrayList<>();
        artistas = new ArrayList<>();
        notificacoes = new ArrayList<>();
        musicas = new ArrayList<>();
        albuns = new ArrayList<>();
        MulticastSocket socket = null;
        System.out.println(this.getName());
        HashMap<String, String> map;

        try {
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println(message);
                //se o id do server for igual ao id do server que vem no pacote, criar nova thread e responder ao pedido
                map = String_To_Hash(message);
                int id;
                if (map.containsKey("mserverid")) {
                    id = Integer.parseInt(map.get("mserverid"));
                    if (id == server_id) {
                        Worker thread = new Worker(map, socket, users, server_id, musicas, artistas, albuns, notificacoes);
                        thread.start();
                    } else {//tratar das cenas de registers e assim, talvez fazer um switch aqui dentro
                        switch(map.get("type")){
                            case "register":
                                //Guardar
                                if (users.isEmpty() == true) {
                                    register_admin(map.get("username"), map.get("password"));
                                    //guardar nos ficheiros
                                    guardarUtilizadores(users);
                                    try {
                                        group = InetAddress.getByName(MULTICAST_ADDRESS);
                                        String aux = ";ID|" + map.get("ID");
                                        String mensagem = "regist_try|sucess;msg|Foi registado como admin!" + aux;
                                        buffer = mensagem.getBytes();
                                        packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                        socket.send(packet);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    for (User u : users) {//Está a dar null pointer exception
                                        if (u.getUsername().equals(map.get("username"))) {//se existir, enviar mensagem a dizer que falhou
                                            try {
                                                group = InetAddress.getByName(MULTICAST_ADDRESS);
                                                String aux = ";ID|" + map.get("ID");
                                                String mensagem = "regist_try|failed"+aux;
                                                buffer = mensagem.getBytes();
                                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                                socket.send(packet);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {//se for bem sucedido, registar e adicionar à base de dados
                                            register(map.get("username"), map.get("password"));
                                            guardarUtilizadores(users);
                                            try {
                                                group = InetAddress.getByName(MULTICAST_ADDRESS);
                                                String aux = ";ID|" + map.get("ID");
                                                String mensagem = "regist_try|sucess"+aux;
                                                buffer = mensagem.getBytes();
                                                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                                socket.send(packet);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                }
                                break;
                        }
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
            r.put(parts2[0], parts2[1]);
        }
        return r;

    }
    public void guardarUtilizadores(ArrayList<User> u) {
        try {
            String filename = "utilizadores" + Integer.toString(server_id) + ".obj";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(u);
            os.close();
        } catch (IOException e) {
            System.out.printf("Ocorreu a exceçao %s ao escrever no ficheiro de objetos dos utilizadores.\n", e);
        }
    }

    void register_admin(String username, String password) {
        User novo;
        novo = new User(username, password, "admin");
        users.add(novo);
    }
    void register(String username, String password) {
        User novo;
        novo = new User(username, password, "normal");
        //adiciona ao array list de utilizadores
        users.add(novo);
    }
}

//Vou ter de pegar no pacote que receber, vou ao protocolo e vou buscar o id do servidor que vai responder
class Worker extends Thread {
    private int server_id;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private ArrayList<User> users;//Lista de utilizadores
    private ArrayList<Musica> musicas;//Lista de musicas
    private ArrayList<Artista> artistas;//Lista de artistas, (um artista pode ser um grupo)
    private ArrayList<Album> albuns;//Lista de albuns
    private ArrayList<Notificacao> notificacoes;//Lista de notificacoes

    private HashMap<String, String> mensagem;

    private MulticastSocket socket;

    Worker(HashMap<String, String> mensagem, MulticastSocket socket, ArrayList<User> users, int server_id, ArrayList<Musica> musicas, ArrayList<Artista> artistas, ArrayList<Album> albuns, ArrayList<Notificacao> notificacoes) {//recebe a mensagem como pedido
        this.mensagem = mensagem;
        this.socket = socket;
        this.users = users;
        this.notificacoes = notificacoes;
        this.albuns = albuns;
        this.artistas = artistas;
        this.musicas = musicas;
        this.server_id = server_id;
    }

    public void run() {
        String aux;
        System.out.println("Thread para tratar do pedido criada!");
        //Tratar da resposta
        switch (mensagem.get("type")) {
            case "login"://para dar login
                String[] logins_bd = new String[2];//para guardar os logins em causa da base de dados e comparar com os da mensagem
                //Vou pegar no username e na password da mensagem e vou ver se está na base de dados
                for (User u : users) {
                    if (u.getUsername().equals(mensagem.get("username"))) {
                        logins_bd[0] = u.getUsername();
                        logins_bd[1] = u.getPassword();
                    }
                }
                //Se estiver, responder com uma mensagem a dizer que foi logado com sucesso
                if (logins_bd[0].equals(mensagem.get("username")) && logins_bd[1].equals(mensagem.get("password"))) {
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        aux = ";ID|" + mensagem.get("ID");
                        String mensagem = "login_try|sucess"+aux;
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //Caso contrário, enviar mensagem a dizer que está incorrecto
                else {
                    //Mandar mensagem "Login incorrecto!"
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        aux = ";ID|" + mensagem.get("ID");
                        String mensagem = "login_try|failed"+aux;
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "register"://para registar, tambem vai ter de ser feito nos outros servidores
                //verificar se o nome já está na base de dados
                //se estiver
                if (users.isEmpty() == true) {
                    register_admin(mensagem.get("username"), mensagem.get("password"));
                    //guardar nos ficheiros
                    guardarUtilizadores(users);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        aux = ";ID|" + mensagem.get("ID");
                        String mensagem = "regist_try|sucess;msg|Foi registado como admin!" + aux;
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (User u : users) {//Está a dar null pointer exception
                        if (u.getUsername().equals(mensagem.get("username"))) {//se existir, enviar mensagem a dizer que falhou
                            try {
                                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                                aux = ";ID|" + mensagem.get("ID");
                                String mensagem = "regist_try|failed"+aux;
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
                                aux = ";ID|" + mensagem.get("ID");
                                String mensagem = "regist_try|sucess"+aux;
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
            case "request_permission_gerir":
                check_permissions_gerir();
                break;
            case "inserir_artista":
                inserir_artista();
                break;
            case "inserir_musica":
                inserir_musica();
                break;
            case "inserir_album":
                inserir_album();
                break;
            case "mostrar_artistas":
                enviar_artistas();
                break;
            case "mostrar_albuns":
                enviar_albuns();
                break;
            case "mostrar_musicas":
                enviar_musicas();
                break;
            case "editar_artista":
                editar_artista();
                break;
            case "editar_musica":
                editar_musica();
                break;
            case "editar_album":
                editar_album();
                break;
            case "remover_artista":
                remover_artista();
                break;
            case "remover_musica":
                remover_musica();
                break;
            case "remover_album":
                remover_album();
                break;
            case "pesquisar":
                pesquisar();
                break;


        }
    }
    /* Função que vai enviar tudo o que tem a string recebida pelo RMI server */
    // Verificar
    public void pesquisar(){
        int counter=0;
        int alb=0,mus=0,art=0;
        String sk=mensagem.get("search_key");// String recebida pelo RMI server
        String aux ="type|whole_list;album_count|";
        String albs=null, muss=null,arts=null;
        //Pesquisar na lista de albuns
        for(Album a:albuns){
            if(a.getNome().toLowerCase().contains(sk.toLowerCase())==true){
                albs=albs+";album_"+Integer.toString(alb)+"_name|"+a.getNome();
                counter++;
                alb++;
            }
        }
        aux=aux+Integer.toString(alb);
        if(albs!=null){
            aux=aux+albs;
        }
        aux=aux+";artista_count|";
        for(Artista a:artistas){
            if(a.getNome().toLowerCase().contains(sk.toLowerCase())==true){
                arts=arts+";artista_"+Integer.toString(art)+"_name|"+a.getNome();
                counter++;
                art++;
            }
        }
        aux=aux+Integer.toString(art);
        if(arts!=null){
            aux=aux+arts;
        }
        aux=aux+";musica_count|";
        for(Musica m:musicas){
            if(m.getNome().toLowerCase().contains(sk.toLowerCase())==true){
                muss=muss+";musica_"+Integer.toString(mus)+"_name|"+m.getNome();
                counter++;
                mus++;
            }
        }
        aux=aux+Integer.toString(mus);
        if(muss!=null){
            aux=aux+muss;
        }
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            String aux2 = mensagem.get("username");
            String aux3 = ";ID|" + mensagem.get("ID");
            String mensagem = "username|" + aux2+aux+aux3;
            byte[] buffer = mensagem.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /* Funções que vão alturar alguns atributos dos artistas,musicas e albuns */

    public void editar_artista(){
        for (Artista a : artistas){
            if(a.getNome().equals(mensagem.get("artista_name"))){//Quando encontra o artista na lista
                Data d;
                String [] as;
                as = mensagem.get("artista_data").split("/");
                d = new Data(Integer.parseInt(as[0]),Integer.parseInt(as[1]),Integer.parseInt(as[2]));
                a.setData_nasc(d);
                a.setGenero(mensagem.get("artista_genero"));
                a.setDescricao(mensagem.get("artista_descricao"));
                break;//pode nao estar bem
            }
        }

    }
    public void editar_musica(){
        for (Musica m : musicas){
            if(m.getNome().equals(mensagem.get("musica_name"))){//Quando encontra a musica na lista
                Data d;
                String [] as;
                as = mensagem.get("musica_data").split("/");
                d = new Data(Integer.parseInt(as[0]),Integer.parseInt(as[1]),Integer.parseInt(as[2]));
                m.setData_lancamento(d);
                m.setDescricao(mensagem.get("musica_descricao"));
                break;//pode nao estar bem
            }
        }
    }
    public void editar_album(){
        for (Album a : albuns){
            if(a.getNome().equals(mensagem.get("album_name"))){//Quando encontra o album na lista
                Data d;
                String [] as;
                as = mensagem.get("album_data").split("/");
                d = new Data(Integer.parseInt(as[0]),Integer.parseInt(as[1]),Integer.parseInt(as[2]));
                a.setData_lancamento(d);
                a.setDescricao(mensagem.get("album_descricao"));
                break;//pode nao estar bem
            }
        }
    }
    /* Funções que vão enviar as listas de artistas,albuns e musicas para o RMI server mostrar ao cliente */

    public void enviar_artistas(){
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            String aux = mensagem.get("username") +";type|artista_list;item_count|"+Integer.toString(artistas.size())+";";
            for(int i=0;i<artistas.size();i++){
                aux=aux+"item_"+Integer.toString(i)+"_name|"+artistas.get(i).getNome();
                if(i!=artistas.size()){
                    aux=aux+";";
                }
            }
            String aux2 = ";ID|" + mensagem.get("ID");
            String mensagem = "username|" + aux + aux2;
            byte[] buffer = mensagem.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enviar_albuns(){
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            String aux = mensagem.get("username") +";type|album_list;item_count|"+Integer.toString(albuns.size())+";";
            for(int i=0;i<albuns.size();i++){
                aux=aux+"item_"+Integer.toString(i)+"_name|"+albuns.get(i).getNome();
                if(i!=albuns.size()){
                    aux=aux+";";
                }
            }
            String aux2 = ";ID|" + mensagem.get("ID");
            String mensagem = "username|" + aux + aux2;
            byte[] buffer = mensagem.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enviar_musicas(){
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            String aux = mensagem.get("username") +";type|music_list;item_count|"+Integer.toString(musicas.size())+";";
            for(int i=0;i<musicas.size();i++){
                aux=aux+"item_"+Integer.toString(i)+"_name|"+musicas.get(i).getNome();
                if(i!=musicas.size()){
                    aux=aux+";";
                }
            }
            String aux2 = ";ID|" + mensagem.get("ID");
            String mensagem = "username|" + aux +aux2;
            byte[] buffer = mensagem.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Função que recebe um username e devolve o tipo de permissoes do utilizador */
    public String tipoUser(String utilizador) {
        for (User u : users) {
            if (u.getUsername().equals(utilizador)) {
                return u.getUsertype();
            }
        }
        return null;
    }
    public void inserir_album(){
        // Vai verificar se o album já existe na base de dados
        // Se já existe
        if(verifica_album(mensagem.get("album_name"))==true){
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_album_try|failed;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se ainda não existe
        else{
            // Criar o novo album
            Data d;
            String [] as;
            as = mensagem.get("album_data").split("/");
            d = new Data(Integer.parseInt(as[0]),Integer.parseInt(as[1]),Integer.parseInt(as[2]));
            Album novo;
            novo = new Album(mensagem.get("album_name"),d,mensagem.get("album_autor"));
            // Vou verificar se o Artista já existe
            // Se não existir o artista
            if(verifica_artista(mensagem.get("album_autor"))==false){
                //Vou adicionar o artista
                Artista a = new Artista(mensagem.get("album_autor"));
                artistas.add(a);
            }
            albuns.add(novo);
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_album_try|sucess;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* Funções para gerir os detalhes de cada lista */
    /* Só os editores e o admin é que o podem fazer */
    /* O acesso a estas funções já está protegido pelo RMI Client */
    public void inserir_artista(){
        // Vai verificar se o artista já existe na base de dados
        // Se já existe
        if(verifica_artista(mensagem.get("artista_name"))==true){
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_artista_try|failed;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se não existir, vai criar
        else{
            // Criar o novo artista
            Data d;
            String [] as;
            as = mensagem.get("artista_data").split("/");
            d = new Data(Integer.parseInt(as[0]),Integer.parseInt(as[1]),Integer.parseInt(as[2]));
            Artista novo;
            novo = new Artista(mensagem.get("artista_name"),d,mensagem.get("artista_descricao"),mensagem.get("artista_genero"));
            // Adicionar à lista
            artistas.add(novo);
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_artista_try|sucess;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void inserir_musica(){
        // Vai verificar se a musica já existe na base de dados
        // Se já existe
        if(verifica_musica(mensagem.get("musica_name"))==true){
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_musica_try|failed;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se não existir, vai criar
        else{
            // Criar a nova musica
            Data d;
            String [] as;
            as = mensagem.get("musica_data").split("/");
            d = new Data(Integer.parseInt(as[0]),Integer.parseInt(as[1]),Integer.parseInt(as[2]));
            Musica novo;
            novo = new Musica(mensagem.get("musica_name"),d,mensagem.get("musica_compositor"),mensagem.get("musica_autor"),mensagem.get("musica_descricao"),mensagem.get("musica_album"));
            // Vou verificar se o Album e o Artista já existem
            // Se não existir o artista
            if(verifica_artista(mensagem.get("musica_autor"))==false){
                //Vou adicionar o artista
                Artista a = new Artista(mensagem.get("musica_autor"));
                artistas.add(a);
            }
            if(verifica_album(mensagem.get("musica_album"))==false){
                //Vou adicionar o album
                Album a = new Album(mensagem.get("musica_album"));
                albuns.add(a);
            }
            musicas.add(novo);
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_artista_try|sucess;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remover_artista(){
        //Verificar se o artista está na lista
        if (verifica_musica(mensagem.get("musica_name"))==true){
            for(Artista a : artistas){
                if(a.getNome().equals(mensagem.get("artista_name"))){
                    artistas.remove(a);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String aux = mensagem.get("username") + ";remove_artista_try|sucess;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                        String mensagem = "username|" + aux;
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;// está aqui bem?
                }

            }
        }
        else{
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";remove_artista_try|failed;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remover_album(){
        //Verificar se o album está na lista
        if (verifica_album(mensagem.get("album_name"))==true){
            for(Album a : albuns){
                if(a.getNome().equals(mensagem.get("album_name"))){
                    albuns.remove(a);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String aux = mensagem.get("username") + ";remove_album_try|sucess;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                        String mensagem = "username|" + aux;
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;// está aqui bem?
                }

            }
        }
        else{
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + "remove_album_try|failed;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remover_musica(){
        //Verificar se o album está na lista
        if (verifica_album(mensagem.get("musica_name"))==true){
            for(Musica m : musicas){
                if(m.getNome().equals(mensagem.get("musica_name"))){
                    musicas.remove(m);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String aux = mensagem.get("username") + "remove_musica_try|sucess;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                        String mensagem = "username|" + aux;
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;// está aqui bem?
                }

            }
        }
        else{
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + "remove_musica_try|failed;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* Função que verifica se o artista já se encontra na base de dados */
    // Recebe o nome do artista, envia true se existir na lista */
    public boolean verifica_artista(String nome_artista){
        if(artistas.isEmpty()){
            return false;
        }
        for(Artista a : artistas){
            if(a.getNome().equals(nome_artista))
                return true;
        }
        return false;
    }
    /* Função que verifica se o album já se encontra na base de dados */
    // Recebe o nome do album, envia true se existir na lista */
    public boolean verifica_album(String nome_album){
        for(Album a : albuns){
            if(a.getNome().equals(nome_album))
                return true;
        }
        return false;
    }
    /* Função que verifica se a musica já se encontra na base de dados */
    // Recebe o nome do artista, envia true se existir na lista */
    public boolean verifica_musica(String nome_musica){
        for(Musica m : musicas){
            if(m.getNome().equals(nome_musica))
                return true;
        }
        return false;
    }


    public void check_permissions_gerir(){
        // Vai verificar as permissoes para entrar na opção de gerir
        // Se tiver permissoes
        if (tipoUser(mensagem.get("username")).equals("editor") || tipoUser(mensagem.get("username")).equals("admin")) {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";acess|granted;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se não tiver permissoes
        else{
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";acess|denied;"+"type|warning"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Quando um cliente dá login, vai enviar uma mensagem para o servidor para ele ver se tem notificacoes
    public void ver_notificacoes() {
        int counter = 0;
        String aux="type|notification_list;item_count|";
        String aux2=null;
        // Procurar em todas as notificacoes se há alguma com o nome do username em causa
        for (Notificacao n : notificacoes) {
            if (n.getDestinario().equals(mensagem.get("username"))) {//se tiver notificacoes para este utilizador
                counter++;
                aux2=aux2+"nota_" + Integer.toString(counter) + "|" + n.getNota() + ";";
                //Remover a notifcacao da array List para não voltar a repetir posteriormente
                notificacoes.remove(n);
            }
        }
        // Se não tiver notificações
        if (counter == 0) {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                aux = mensagem.get("username") + "type|warning;msg|Nao tem notificacoes!"+ ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se tiver notificações
        else{
            String message=aux+Integer.toString(counter)+aux2+ ";ID|" + mensagem.get("ID");;
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Metodo que vai dar permissoes a outros utilizadores
    public void make_editor() {
        String aux;
        // Vai verificar se o user em questao tem permissao de admin ou utilizador
        // Nao tem permissao, enviar mensagem
        if (tipoUser(mensagem.get("username")).equals("normal")) {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String mensagem = "acess|denied;msg|Nao tem permissoes";
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (tipoUser(mensagem.get("username")).equals("admin") || tipoUser(mensagem.get("username")).equals("editor")) {
            //Ver a que utilizador quer dar permissoes
            for (User u : users) {
                if (u.getUsername().equals(mensagem.get("editor_name"))) {//se encontrou o nome
                    if (u.getUsertype().equals("editor") || u.getUsertype().equals("admin")) {//se já tem permissoes
                        try {
                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                            String mensagem = "msg|O utilizador já tem permissões!";
                            byte[] buffer = mensagem.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {//se ainda não tem permissões
                        try {
                            u.setUsertype("editor");//altera as permissoes
                            aux = "editor_made|" + mensagem.get("username");
                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                            String mensagem = "msg|Permissoes atualizadas!;" + aux;
                            byte[] buffer = mensagem.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {//se não encontrou o nome
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String mensagem = "msg|O utilizador não existe!";
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //Função que vai enviar por mensagem a lista de todos os albuns
    //Falta testar
    public void criticar_album() {
        int tamanho = albuns.size();
        //Se não houver album nenhum
        if (tamanho == 0) {
            //Enviar mensagem a dizer que não há albuns
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String mensagem = "type|warning;msg|Não há albuns disponveis!!";
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            String mensagem = "type|album_list;item_count|" + Integer.toString(tamanho) + ";";
            for (int i = 0; i < albuns.size(); i++) {
                mensagem = mensagem + "item_" + Integer.toString(i) + "_name|" + albuns.get(i).getNome() + ";";
            }
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void criticar_album2() {
        //Vai receber o album que o utilizador quer criticar, a sua mensagem e a cotacao
        ArrayList<Critica> criticas;
        for (Album a : albuns) {
            if (a.getNome().equals(mensagem.get("item_0_name"))) {//Quando encontra o album na lista de albuns
                Critica c = new Critica(mensagem.get("review_txt"), Integer.parseInt(mensagem.get("review_avaliacao")));
                criticas = a.getCriticas();
                criticas.add(c);
                a.setCriticas(criticas);
            }
        }
        try {
            String mensagem = "type|warning;msg|Critica escrita com sucesso!";
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            byte[] buffer = mensagem.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Função para registar o admin, só vai ser utilizada uma vez
    void register_admin(String username, String password) {
        User novo;
        novo = new User(username, password, "admin");
        users.add(novo);
    }

    //Funcao para registar pessoa
    void register(String username, String password) {
        User novo;
        novo = new User(username, password, "normal");
        //adiciona ao array list de utilizadores
        users.add(novo);
    }
    /* Funções para lidar com ficheiros */
    /* Falta testar */

    void leObjetosArtistas() {
        ObjectInputStream ois = null;
        try {
            String filename = "artistas" + Integer.toString(server_id) + ".obj";
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
        } catch (IOException e) {
            System.out.println(" ");
        }
        if (ois != null) {
            try {
                artistas = (ArrayList<Artista>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    void leObjetosMusicas() {
        ObjectInputStream ois = null;
        try {
            String filename = "musicas" + Integer.toString(server_id) + ".obj";
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
        } catch (IOException e) {
            System.out.println(" ");
        }
        if (ois != null) {
            try {
                musicas = (ArrayList<Musica>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    void leObjetosNotificacoes() {
        ObjectInputStream ois = null;
        try {
            String filename = "notificacoes" + Integer.toString(server_id) + ".obj";
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
        } catch (IOException e) {
            System.out.println(" ");
        }
        if (ois != null) {
            try {
                notificacoes = (ArrayList<Notificacao>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    void leObjetosUtilizadores() {
        ObjectInputStream ois = null;
        try {
            String filename = "utilizadores" + Integer.toString(server_id) + ".obj";
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
        } catch (IOException e) {
            System.out.println(" ");
        }
        if (ois != null) {
            try {
                users = (ArrayList<User>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void guardarUtilizadores(ArrayList<User> u) {
        try {
            String filename = "utilizadores" + Integer.toString(server_id) + ".obj";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(u);
            os.close();
        } catch (IOException e) {
            System.out.printf("Ocorreu a exceçao %s ao escrever no ficheiro de objetos dos utilizadores.\n", e);
        }
    }

    public void guardarArtistas(ArrayList<Artista> a) {
        try {
            String filename = "artistas" + Integer.toString(server_id) + ".obj";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(a);
            os.close();
        } catch (IOException e) {
            System.out.printf("Ocorreu a exceçao %s ao escrever no ficheiro de objetos dos artistas.\n", e);
        }
    }

    public void guardarNotificacoes(ArrayList<Notificacao> n) {
        try {
            String filename = "notificacoes" + Integer.toString(server_id) + ".obj";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(n);
            os.close();
        } catch (IOException e) {
            System.out.printf("Ocorreu a exceçao %s ao escrever no ficheiro de objetos das notificacoes.\n", e);
        }
    }

    public void guardarMusicas(ArrayList<Musica> m) {
        try {
            String filename = "musicas" + Integer.toString(server_id) + ".obj";
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(m);
            os.close();
        } catch (IOException e) {
            System.out.printf("Ocorreu a exceçao %s ao escrever no ficheiro de objetos dos musicas.\n", e);
        }
    }
}

