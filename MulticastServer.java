import java.io.Serializable;
import java.net.*;
import java.io.IOException;
import java.lang.*;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.io.FileNotFoundException;


public class MulticastServer extends Thread implements Serializable {
    private static int server_id;
    private static int TCPPort;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private ArrayList<User> users;//Lista de utilizadores
    private ArrayList<Musica> musicas;//Lista de musicas
    private ArrayList<Musico> Musicos;//Lista de Musicos, (um Musico pode ser um grupo)
    private ArrayList<Album> albuns;//Lista de albuns
    private ArrayList<Notificacao> notificacoes;//Lista de notificacoes
    private HashMap<String, String> map;
    private MulticastSocket socket;
    private ServerSocket socketTCP;
    private Socket socketClient; //Socket para dar o accept
    static final long serialVersionUID = 42L;// Para resolver warning
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String password = "admin";


    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conectado à base de dados com sucesso!");
        } catch (SQLException e) {
            System.out.println("Ligação com a base de dados falhada!");
        }

        return conn;
    }

    public Connection testConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Ligação com a base de dados perdida!");
        }

        return conn;
    }
    public int findID(String nome) {
        String SQL = "SELECT idartista, "
                + "FROM artista "
                + "WHERE nome = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return 0;
    }
    public void testefuncao(String nome,int id_grupo) {
        /*
        INSERT INTO bar (description, foo_id) VALUES
                ( 'testing',     (SELECT id from foo WHERE type='blue') ),
        ( 'another row', (SELECT id from foo WHERE type='red' ) );
        */
        String SQL = "INSERT INTO grupo_musico(grupo_artista_idartista,musico_artista_idartista) "
                + "VALUES (?, (SELECT artista_idartista FROM musico WHERE artista_idartista = (SELECT idartista FROM artista WHERE nome = ?))) ";


        try (Connection conn = testConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, id_grupo);
            pstmt.setString(2,nome);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    /* Funções para inserir um Musico na BD */

    public void insertArtista(Musico m1) {
        String SQL = "INSERT INTO artista(nome,genero,descricao,data) "
                + "VALUES(?,?,?,?)";
        int id = 0;// Era long, pode dar problemas em ser int

        try (Connection conn = testConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL,Statement.RETURN_GENERATED_KEYS) ){

            pstmt.setString(1, m1.getNome());
            pstmt.setString(2, m1.getGenero());
            pstmt.setString(3, m1.getDescricao());
            pstmt.setDate(4,new Date(m1.getData_criacao().getAno(),m1.getData_criacao().getMes(),m1.getData_criacao().getDia()));

            int affectedRows = pstmt.executeUpdate();


            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(5);
                        System.out.println("Estou a imprimir o ID:"+ id);
                        insertMusico(m1,id);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void insertMusico(Musico m1,int id){
        String SQL = "INSERT INTO musico(datanascimento,artista_idartista) "
                + "VALUES(?,?)";

        try (Connection conn = testConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL) ){

            pstmt.setDate(1, new Date(m1.getDataNascimento().getAno(),m1.getDataNascimento().getMes(),m1.getDataNascimento().getDia()));
            pstmt.setInt(2,id);
            pstmt.executeUpdate();


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    /* Funções para inserir um Grupo na BD */
    public void insertArtista(Grupo g1) {
        String SQL = "INSERT INTO artista(nome,genero,descricao,data) "
                + "VALUES(?,?,?,?)";
        int id = 0;

        try (Connection conn = testConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL,Statement.RETURN_GENERATED_KEYS) ){

            pstmt.setString(1, g1.getNome());
            pstmt.setString(2, g1.getGenero());
            pstmt.setString(3, g1.getDescricao());
            pstmt.setDate(4,new Date(g1.getData_criacao().getAno(),g1.getData_criacao().getMes(),g1.getData_criacao().getDia()));

            int affectedRows = pstmt.executeUpdate();


            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(5);
                        insertGrupo(g1,id);
                        for(String s : g1.getConstituintes()){
                            testefuncao(s,id);
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void insertGrupo(Grupo g1,int id){
        String SQL = "INSERT INTO grupo(artista_idartista) "
                + "VALUES(?)";

        try (Connection conn = testConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL) ){

            pstmt.setInt( 1,id);
            pstmt.executeUpdate();


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public static void main(String[] args) { //meter um id do servidor
        String teste = args[0];
        String teste1 = args[1];
        server_id = Integer.parseInt(teste);
        TCPPort= Integer.parseInt(teste1);
        MulticastServer server = new MulticastServer();
        server.connect();
        Musico funfa = new Musico("MusicoNormal1", new Data(1,5,1998),"Esta merda funcionou", "Rock",new Data(11,05,2018));
        Musico funfa2 = new Musico("MusicoNormal2", new Data(1,5,1998),"Esta merda funcionou", "Rock",new Data(11,05,2018));
        Grupo testeGrupo = new Grupo("testeGrupo2", new Data(1,5,1998),"Podemos avançar", "Funk");
        ArrayList <String> auxiliar = new ArrayList<>();
        server.insertArtista(funfa);
        server.insertArtista(funfa2);
        auxiliar.add("MusicoNormal1");
        auxiliar.add("MusicoNormal2");
        testeGrupo.setConstituintes(auxiliar);
        server.insertArtista(testeGrupo);
        server.start();
    }

    public MulticastServer() {
        super("Server running " + server_id);
    }

    public void run() {
        users = new ArrayList<>();
        Musicos = new ArrayList<>();
        notificacoes = new ArrayList<>();
        musicas = new ArrayList<>();
        albuns = new ArrayList<>();
        socket = null;
        System.out.println(this.getName());
        String aux;
        try {
            socket = new MulticastSocket(PORT);
            socketTCP = new ServerSocket(TCPPort);
            Helper h = new Helper(server_id,socket);
            h.start();
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
                        Worker thread = new Worker(map, socket, users, server_id, musicas, Musicos, albuns, notificacoes,TCPPort,socketTCP);
                        thread.start();
                    } else {//tratar das cenas de registers e assim, talvez fazer um switch aqui dentro, sem mandar mensagens
                        switch (map.get("type")) {
                            case "register"://para registar, tambem vai ter de ser feito nos outros servidores
                                int flag=0;
                                //verificar se o nome já está na base de dados
                                //se estiver
                                if (users.isEmpty()) { // Se estiver vazio crio como admin
                                    register_admin(map.get("username"), map.get("password"));
                                    //guardar nos ficheiros
                                } else { // Se não estiver vazio
                                    Iterator<User> it = users.iterator();// Cria o iterador
                                    while (it.hasNext()) {
                                        User u = it.next();
                                        if (u.getUsername().equals(map.get("username"))) {//se existir, enviar mensagem a dizer que falhou
                                            flag=1;
                                        }

                                    }
                                    if(flag==0) {
                                        register(map.get("username"), map.get("password"));
                                    }
                                }
                                break;
                            case "inserir_Musico":
                                //inserir_Musico();
                                break;
                            case "inserir_album":
                                //inserir_album();
                                break;
                            case "inserir_musica":
                                inserir_musica();
                                break;
                            case "editar_Musico":
                                editar_Musico();
                                break;
                            case "editar_musica":
                                editar_musica();
                                break;
                            case "editar_album":
                                editar_album();
                                break;
                            case "write_review":
                                criticar_album2();
                                break;
                            case "make_editor":
                                make_editor();
                                break;
                            case "new_notification":
                                receber_notificacoes();
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
    public void receber_notificacoes(){
        Notificacao n = new Notificacao(map.get("notification"),map.get("username"));
        notificacoes.add(n);
    }
    public void make_editor() {
        // Vai verificar se o user em questao tem permissao de admin ou utilizador
        if (tipoUser(map.get("username")).equals("admin") || tipoUser(map.get("username")).equals("editor")) {
            //Ver a que utilizador quer dar permissoes
            for (User u : users) {
                if (u.getUsername().equals(map.get("editor_name"))) {// Se encontrou o nome
                    if (u.getUsertype().equals("editor") || u.getUsertype().equals("admin")) {//se já tem permissoes
                    } else {//se ainda não tem permissões
                        u.setUsertype("editor");//altera as permissoes
                    }
                    break;
                }
            }
        }
    }
    public void editar_Musico() {
        for (Musico a : Musicos) {
            if (a.getNome().equals(map.get("Musico_name"))) {//Quando encontra o Musico na lista
                Data d;
                String[] as;
                as = map.get("Musico_data").split("/");
                d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
                a.setData_nasc(d);
                a.setGenero(map.get("Musico_genero"));
                a.setDescricao(map.get("Musico_descricao"));
                break;//pode nao estar bem
            }
        }

    }

    public void editar_musica() {
        for (Musica m : musicas) {
            if (m.getNome().equals(map.get("musica_name"))) {//Quando encontra a musica na lista
                Data d;
                String[] as;
                as = map.get("musica_data").split("/");
                d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
                m.setData_lancamento(d);
                m.setDescricao(map.get("musica_descricao"));
                break;//pode nao estar bem
            }
        }
    }

    public void editar_album() {
        for (Album a : albuns) {
            if (a.getNome().equals(map.get("album_name"))) {//Quando encontra o album na lista
                Data d;
                String[] as;
                as = map.get("album_data").split("/");
                d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
                a.setData_lancamento(d);
                a.setDescricao(map.get("album_descricao"));
                if(!a.getPessoas_descricoes().contains(map.get("username"))) {
                    a.getPessoas_descricoes().add(map.get("username"));
                }
                break;//pode nao estar bem
            }
        }
    }
    /*
    public void inserir_album() {
        // Vai verificar se o album já existe na base de dados
        // Se ainda não existe
        if (verifica_album(map.get("album_name")) == false){
            // Criar o novo album
            Data d;
            String[] as;
            as = map.get("album_data").split("/");
            d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
            Album novo;
            ArrayList<Musica> musicas_album = new ArrayList<>();
            ArrayList<String> pessoas_descricoes = new ArrayList<>();
            ArrayList<Critica> criticas = new ArrayList<>();
            novo = new Album(map.get("album_name"), d, map.get("album_autor"), musicas_album, pessoas_descricoes,criticas);
            // Vou verificar se o Musico já existe
            // Se não existir o Musico
            if (verifica_Musico(map.get("album_autor")) == false) {
                //Vou adicionar o Musico
                ArrayList<Album> lista_albuns = new ArrayList<>();
                Musico a = new Musico(map.get("album_autor"), lista_albuns);
                //Criar a lista de albuns
                Musicos.add(a);
            }
            albuns.add(novo);
            // Adicionar o album à lista de albuns do Musico
            for (Musico a : Musicos) {
                if (a.getNome().equals(map.get("album_autor"))) {// Quando encontrar o Musico
                    //Adicionar o album ao Musico
                    a.getListaAlbuns().add(novo);

                }
            }
        }
    }
    */

    /* Funções para gerir os detalhes de cada lista */
    /* Só os editores e o admin é que o podem fazer */
    /* O acesso a estas funções já está protegido pelo RMI Client */
    /*
    public void inserir_Musico() {
        // Se não existir, vai criar
        if (verifica_Musico(map.get("Musico_name")) == false) {
            // Criar o novo Musico
            Data d;
            String[] as;
            as = map.get("Musico_data").split("/");
            d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
            Musico novo;
            ArrayList<Album> lista_albuns = new ArrayList<>();
            novo = new Musico(map.get("Musico_name"), d, map.get("Musico_descricao"), map.get("Musico_genero"), lista_albuns);
            // Adicionar à lista
            Musicos.add(novo);
        }

    }
    */
    public String tipoUser(String utilizador) {
        for (User u : users) {
            if (u.getUsername().equals(utilizador)) {
                return u.getUsertype();
            }
        }
        return null;
    }

    public void inserir_musica() {
        // Vai verificar se a musica já existe na base de dados
        // Se já existe
        if (verifica_musica(map.get("musica_name")) == false){
            // Criar a nova musica
            Data d;
            String[] as;
            as = map.get("musica_data").split("/");
            d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
            Musica novo;
            novo = new Musica(map.get("musica_name"), d, map.get("musica_compositor"), map.get("musica_autor"), map.get("musica_descricao"), map.get("musica_album"));
            // Vou verificar se o Album e o Musico já existem
            // Se não existir o Musico
            if (verifica_Musico(map.get("musica_autor")) == false) {
                //Vou adicionar o Musico
                Musico a = new Musico(map.get("musica_autor"));
                Musicos.add(a);
            }
            if (verifica_album(map.get("musica_album")) == false) {
                //Vou adicionar o album
                Album a = new Album(map.get("musica_album"));
                albuns.add(a);
            }
            //Procurar o album na lista de albuns e adicionar a musica
            for (Album a : albuns) {
                if (a.getNome().equals(map.get("musica_album"))) {// Quando encontrar o album
                    //Adicionar a musica ao album
                    a.getMusicas().add(novo);
                }
            }
            musicas.add(novo);
        }
    }
    // Por iterador
    public void remover_Musico() {
        //Verificar se o Musico está na lista
        if (verifica_Musico(map.get("Musico_name")) == true) {
            Iterator<Musico> it = Musicos.iterator();// Cria o iterador
            while (it.hasNext()) {
                Musico a = it.next();
                if (a.getNome().equals(map.get("Musico_name"))) {
                    Musicos.remove(a);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String aux = map.get("username") + ";remove_Musico_try|sucess;" + "type|warning" + ";ID|" + map.get("ID");
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
    }

    public void remover_album() {
        //Verificar se o album está na lista
        if (verifica_album(map.get("album_name")) == true) {
            Iterator<Album> it = albuns.iterator();// Cria o iterador
            while (it.hasNext()) {
                Album a = it.next();
                if (a.getNome().equals(map.get("album_name"))) {
                    albuns.remove(a);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String aux = map.get("username") + ";remove_album_try|sucess;" + "type|warning" + ";ID|" + map.get("ID");
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
    }

    public void remover_musica() {
        //Verificar se o album está na lista
        if (verifica_musica(map.get("musica_name")) == true) {
            Iterator<Musica> it = musicas.iterator();// Cria o iterador
            while (it.hasNext()) {
                Musica m = it.next();
                if (m.getNome().equals(map.get("musica_name"))) {
                    musicas.remove(m);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String aux = map.get("username") + "remove_musica_try|sucess;" + "type|warning" + ";ID|" + map.get("ID");
                        String mensagem = "username|" + aux;
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }
    }

    /* Função que verifica se o Musico já se encontra na base de dados */
    // Recebe o nome do Musico, envia true se existir na lista */
    public boolean verifica_Musico(String nome_Musico) {
        if (Musicos.isEmpty()) {
            return false;
        }
        for (Musico a : Musicos) {
            if (a.getNome().equals(nome_Musico))
                return true;
        }
        return false;
    }

    /* Função que verifica se o album já se encontra na base de dados */
    // Recebe o nome do album, envia true se existir na lista */
    public boolean verifica_album(String nome_album) {
        for (Album a : albuns) {
            if (a.getNome().equals(nome_album))
                return true;
        }
        return false;
    }

    /* Função que verifica se a musica já se encontra na base de dados */
    // Recebe o nome do Musico, envia true se existir na lista */
    public boolean verifica_musica(String nome_musica) {
        for (Musica m : musicas) {
            if (m.getNome().equals(nome_musica))
                return true;
        }
        return false;
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

    void criticar_album2() {
        //Vai receber o album que o utilizador quer criticar, a sua mensagem e a cotacao
        ArrayList<Critica> criticas;
        for (Album a : albuns) {
            if (a.getNome().equals(map.get("album_name"))) {//Quando encontra o album na lista de albuns
                Critica c = new Critica(map.get("review_critica"), Integer.parseInt(map.get("review_pontuacao")));
                criticas = a.getCriticas();
                criticas.add(c);
                a.setCriticas(criticas);
            }
        }
    }
}

class Worker extends Thread {
    private int server_id;
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private ArrayList<User> users;//Lista de utilizadores
    private ArrayList<Musica> musicas;//Lista de musicas
    private ArrayList<Musico> Musicos;//Lista de Musicos, (um Musico pode ser um grupo)
    private ArrayList<Album> albuns;//Lista de albuns
    private ArrayList<Notificacao> notificacoes;//Lista de notificacoes
    private int TCPPort;


    private HashMap<String, String> mensagem;

    private MulticastSocket socket;
    private ServerSocket socketTCP;

    Worker(HashMap<String, String> mensagem, MulticastSocket socket, ArrayList<User> users, int server_id, ArrayList<Musica> musicas, ArrayList<Musico> Musicos, ArrayList<Album> albuns, ArrayList<Notificacao> notificacoes,int TCPPort,ServerSocket socketTCP) {//recebe a mensagem como pedido
        this.mensagem = mensagem;
        this.socket = socket;
        this.users = users;
        this.notificacoes = notificacoes;
        this.albuns = albuns;
        this.Musicos = Musicos;
        this.musicas = musicas;
        this.server_id = server_id;
        this.TCPPort=TCPPort;
        this.socketTCP=socketTCP;
    }

    public void run() {
        String aux;
        System.out.println("Thread para tratar do pedido criada!");
        //Tratar da resposta
        switch (mensagem.get("type")) {
            case "login"://para dar login
                String[] logins_bd = new String[2];//para// os logins em causa da base de dados e comparar com os da mensagem
                logins_bd[0]="";
                logins_bd[1]="";
                //Vou pegar no username e na password da mensagem e vou ver se está na base de dados
                for (User u : users) {
                    if (u.getUsername().equals(mensagem.get("username"))) {
                        logins_bd[0] = u.getUsername();
                        logins_bd[1] = u.getPassword();
                    }
                }
                // Se estiver, responder com uma mensagem a dizer que foi logado com sucesso
                // Mandar também as notificações do user
                if (logins_bd[0].equals(mensagem.get("username")) && logins_bd[1].equals(mensagem.get("password"))) {
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        aux = ";ID|" + mensagem.get("ID");
                        String notas;
                        notas = notificacoesUser1(mensagem.get("username"));
                        String mensagem = "login_try|sucess" + aux + notas;
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
                        String mensagem = "login_try|failed" + aux;
                        byte[] buffer = mensagem.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "register":
                int flag=0;
                //verificar se o nome já está na base de dados
                //se estiver
                if (users.isEmpty() == true) { // Se estiver vazio crio como admin
                    register_admin(mensagem.get("username"), mensagem.get("password"));
                    //guardar nos ficheiros
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
                } else { // Se não estiver vazio
                    Iterator<User> it = users.iterator();// Cria o iterador
                    while (it.hasNext()) {//Está a dar concurrentModificationException, usar iterator
                        User u = it.next();
                        if (u.getUsername().equals(mensagem.get("username"))) {//se existir, enviar mensagem a dizer que falhou
                            try {
                                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                                aux = ";ID|" + mensagem.get("ID");
                                String mensagem = "regist_try|failed" + aux;
                                byte[] buffer = mensagem.getBytes();
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                                socket.send(packet);
                                flag=1;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }

                    }
                    if(flag==0) {
                        register(mensagem.get("username"), mensagem.get("password"));
                        try {
                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                            aux = ";ID|" + mensagem.get("ID");
                            String mensagem = "regist_try|sucess" + aux;
                            byte[] buffer = mensagem.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case "write_review":
                criticar_album2();
                break;
            case "make_editor":
                make_editor();
                break;
            case "request_permission_gerir":
                check_permissions_gerir();
                break;
            case "inserir_Musico":
                //inserir_Musico();
                break;
            case "inserir_musica":
                inserir_musica();
                break;
            case "inserir_album":
                //inserir_album();
                break;
            case "mostrar_Musicos":
                enviar_Musicos();
                break;
            case "mostrar_albuns":
                enviar_albuns();
                break;
            case "mostrar_musicas":
                enviar_musicas();
                break;
            case "editar_Musico":
                editar_Musico();
                break;
            case "editar_musica":
                editar_musica();
                break;
            case "editar_album":
                editar_album();
                break;
            case "remover_Musico":
                remover_Musico();
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
            case "get_musica":
                get_musica();
                break;
            case "get_album":
                get_album();
                break;
            case "get_Musico":
                //get_Musico();
                break;
            case "new_notification":
                receber_notificacoes();
                break;
            case "upload_music":
                upload_music();
                break;
        }
    }
    // É possível a cada utilizador dar upload de uma música que ficará associado a uma música específica
    // Inicialmente fica restrita à conta do próprio utilizador
    public void upload_music(){
        // Verificar se a música já existe
        // Se já existir
        String aux;
        if (verifica_musica(mensagem.get("musica_name")) == true){
            // Vai ter de enviar o IP da máquina e a porta
            try {
                InetAddress localhost = InetAddress.getLocalHost();
                String filename=mensagem.get("musica_name");
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                aux = ";ID|" + mensagem.get("ID");
                String mensagem = "type|tcp_info;ip_server|" +localhost.getHostAddress()+";port_server|"+Integer.toString(TCPPort)+ aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                // Criar a thread para tratar da ligação
                ConnectionTCP c;
                c= new ConnectionTCP(socketTCP,filename);
                c.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se não existir, enviar mensagem a dizer para o utilizador criar a música
        else{
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                aux = ";ID|" + mensagem.get("ID");
                String mensagem = "type|warning;msg|Esta música não existe na base de dados, tem de criar a música primeiro" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    // Função que vai receber as notificações e as vai adicionar ao array de notificações
    public void receber_notificacoes(){
        Notificacao n = new Notificacao(mensagem.get("notification"),mensagem.get("username"));
        notificacoes.add(n);
        //Mandar mensagem para facilitar o RMI server
        try{
            String aux="";
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            aux="type|warning;msg|Notificação adicionada!;ID|"+mensagem.get("ID");
            byte[] buffer = aux.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String notificacoesUser1(String user) {
        int counter = 0;
        String aux = ";notification_count|";
        String aux2 = "";
        Iterator<Notificacao> iterador = notificacoes.iterator();
        while (iterador.hasNext()) {// Este iterador pode estar mal, a função original está na secretaria
            Notificacao n = iterador.next();
            if (n.getDestinario().equals(user)) {//se tiver notificacoes para este utilizador
                aux2 += ";notification_" + Integer.toString(counter) + "|" + n.getNota();
                counter++;
                //Remover a notifcacao da array List para não voltar a repetir posteriormente
                iterador.remove();
            }
        }
        aux += Integer.toString(counter) + aux2;
        return aux;
    }

    public double calculo_pontuacao(ArrayList<Critica> criticas) {
        if (criticas.isEmpty())
            return 0;
        int acum = 0;
        for (Critica c : criticas) {
            acum += c.getAvaliacao();
        }
        return (double)(acum / criticas.size());

    }

    // Vai receber o nome da música e vai devolver todos os atributos
    public void get_musica() {
        //Vou procurar a musica
        String aux = "";
        for (Musica m : musicas) {
            if (m.getNome().equals(mensagem.get("musica_name"))) {// Quando encontrar
                aux = "username|" + mensagem.get("username") + ";type|musica_info;musica_name|" + m.getNome() + ";musica_autor|" + m.getAutor() + ";musica_compositor|" + m.getCompositor() + ";musica_data|" + m.getData_lancamento().getDia() + "/" + m.getData_lancamento().getMes() + "/" + m.getData_lancamento().getAno() + ";musica_descricao|" + m.getDescricao() + ";musica_album|" + m.getAlbum() + ";ID|" + mensagem.get("ID");
                break;
            }
        }
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            byte[] buffer = aux.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Vai receber o nome do album e devolve os seus atributos (criticas, musicas, etc)
    public void get_album() {
        //Vou procurar o album
        String aux = "";
        String auxC = "";
        String auxM = "";
        int counter = 0;
        ArrayList<Critica> auxCriticas;
        ArrayList<Musica> auxMusicas;
        for (Album a : albuns) {
            if (a.getNome().equals(mensagem.get("album_name"))) {// Quando encontrar
                aux = "username|" + mensagem.get("username") + ";type|album_info;album_name|" + a.getNome() + ";";
                if (a.getData_lancamento() != null) {
                    aux += "album_data|" + a.getData_lancamento().getDia() + "/" + a.getData_lancamento().getMes() + "/" + a.getData_lancamento().getAno() + ";";
                }
                if (a.getDescricao() != null) {
                    aux += "album_descricao|" + a.getDescricao() + ";";
                }
                if (a.getAutor() != null) {
                    aux += "album_autor|" + a.getAutor() + ";";
                }
                if (a.getCriticas() != null) {
                    auxCriticas = a.getCriticas();
                    auxC = "criticas_count|" + Integer.toString(auxCriticas.size()) + ";";
                    for (Critica c : auxCriticas) {
                        auxC += "critica_" + Integer.toString(counter) + "_string|" + c.getJustificao() + ";critica_" + Integer.toString(counter) + "_nota|" + Integer.toString(c.getAvaliacao()) + ";";
                        counter++;
                    }
                    aux += auxC;
                    aux += "album_med|" + String.valueOf(calculo_pontuacao(auxCriticas)) + ";";
                    counter = 0;
                }
                if (a.getMusicas() != null) {
                    auxMusicas = a.getMusicas();
                    auxM = "musica_count|" + Integer.toString(auxMusicas.size()) + ";";
                    for (Musica m : auxMusicas) {
                        auxM += "musica_" + Integer.toString(counter) + "_name|" + m.getNome() + ";";
                        counter++;
                    }
                }
                aux += auxM;
                aux += "ID|" + mensagem.get("ID");
                break;// Está aqui bem?
            }
        }
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            byte[] buffer = aux.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Vai receber o nome do Musico e vai devolver os nomes dos albuns e os atributos do Musico
    /*
    public void get_Musico() {
        String aux = "";
        // Vou procurar o Musico
        ArrayList<Album> auxAlbum;
        String auxA = "";
        int counter = 0;
        for (Musico a : Musicos) {
            if (a.getNome().equals(mensagem.get("Musico_name"))) {// Quando encontrar
                aux = "username|" + mensagem.get("username") + ";type|Musico_info;Musico_name|" + a.getNome() + ";";
                if (a.getData_nasc() != null) {
                    aux += "Musico_data|" + a.getData_nasc().getDia() + "/" + a.getData_nasc().getMes() + "/" + a.getData_nasc().getAno() + ";";
                }
                if (a.getGenero() != null) {
                    aux += "Musico_genero|" + a.getGenero() + ";";
                }
                if (a.getDescricao() != null) {
                    aux += "Musico_descricao|" + a.getDescricao() + ";";
                }
                if (a.getListaAlbuns() != null) {
                    auxAlbum = a.getListaAlbuns();
                    auxA = "albuns_count|" + Integer.toString(auxAlbum.size()) + ";";
                    for (Album al : auxAlbum) {
                        auxA += "album_" + Integer.toString(counter) + "_name|" + al.getNome() + ";";
                        counter++;
                    }
                }
                aux += auxA;
                aux += "ID|" + mensagem.get("ID");
                break;
            }
        }
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            byte[] buffer = aux.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    /* Função que vai enviar tudo o que tem a string recebida pelo RMI server */
    // Verificar
    public void pesquisar() {
        int counter = 0;
        int alb = 0, mus = 0, art = 0;
        String sk = mensagem.get("search_key");// String recebida pelo RMI server
        String aux = "type|whole_list;album_count|";
        String albs = "", muss = "", arts = "";
        //Pesquisar na lista de albuns
        for (Album a : albuns) {
            if (a.getNome().toLowerCase().contains(sk.toLowerCase()) == true) {
                albs = albs + ";album_" + Integer.toString(alb) + "_name|" + a.getNome();
                counter++;
                alb++;
            }
        }
        aux = aux + Integer.toString(alb);
        if (albs != null) {
            aux = aux + albs;
        }
        aux = aux + ";Musico_count|";
        for (Musico a : Musicos) {
            if (a.getNome().toLowerCase().contains(sk.toLowerCase()) == true) {
                arts = arts + ";Musico_" + Integer.toString(art) + "_name|" + a.getNome();
                counter++;
                art++;
            }
        }
        aux = aux + Integer.toString(art);
        if (arts != null) {
            aux = aux + arts;
        }
        aux = aux + ";musica_count|";
        for (Musica m : musicas) {
            if (m.getNome().toLowerCase().contains(sk.toLowerCase()) == true) {
                muss = muss + ";musica_" + Integer.toString(mus) + "_name|" + m.getNome();
                counter++;
                mus++;
            }
        }
        aux = aux + Integer.toString(mus);
        if (muss != null) {
            aux = aux + muss;
        }
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            String aux2 = mensagem.get("username") + ";";
            String aux3 = ";ID|" + mensagem.get("ID");
            String mensagem = "username|" + aux2 + aux + aux3;
            byte[] buffer = mensagem.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /* Funções que vão alturar alguns atributos dos Musicos,musicas e albuns */

    public void editar_Musico() {
        for (Musico a : Musicos) {
            if (a.getNome().equals(mensagem.get("Musico_name"))) {//Quando encontra o Musico na lista
                Data d;
                String[] as;
                as = mensagem.get("Musico_data").split("/");
                d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
                a.setData_nasc(d);
                a.setGenero(mensagem.get("Musico_genero"));
                a.setDescricao(mensagem.get("Musico_descricao"));
                break;//pode nao estar bem
            }
        }
    }

    public void editar_musica() {
        for (Musica m : musicas) {
            if (m.getNome().equals(mensagem.get("musica_name"))) {//Quando encontra a musica na lista
                Data d;
                String[] as;
                as = mensagem.get("musica_data").split("/");
                d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
                m.setData_lancamento(d);
                m.setDescricao(mensagem.get("musica_descricao"));
                break;//pode nao estar bem
            }
        }
    }

    public void editar_album() {
        for (Album a : albuns) {
            if (a.getNome().equals(mensagem.get("album_name"))) {//Quando encontra o album na lista
                Data d;
                String[] as;
                as = mensagem.get("album_data").split("/");
                d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
                a.setData_lancamento(d);
                a.setDescricao(mensagem.get("album_descricao"));
                if(!a.getPessoas_descricoes().contains(mensagem.get("username"))) {
                    a.getPessoas_descricoes().add(mensagem.get("username"));
                }
                try{
                    String aux="type|warning"+";ID|" + mensagem.get("ID")+";notification|Detalhes do album "+mensagem.get("album_name")+ " alterado";
                    String aux2=";user_count|";
                    int counter=0;
                    InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                    ArrayList<String> pessoas=a.getPessoas_descricoes();
                    aux2+=Integer.toString(pessoas.size());
                    for(String s:pessoas){
                        aux2 += ";user_" + Integer.toString(counter) + "_name|" + s;
                        counter++;
                    }
                    aux+=aux2;
                    byte[] buffer = aux.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                }catch (IOException e) {
                    e.printStackTrace();
                }
                break;//pode nao estar bem
            }
        }
    }

    /* Funções que vão enviar as listas de Musicos,albuns e musicas para o RMI server mostrar ao cliente */
    public void enviar_Musicos() {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            String aux = mensagem.get("username") + ";type|Musico_list;item_count|" + Integer.toString(Musicos.size()) + ";";
            for (int i = 0; i < Musicos.size(); i++) {
                aux = aux + "item_" + Integer.toString(i) + "_name|" + Musicos.get(i).getNome();
                if (i != Musicos.size()) {
                    aux = aux + ";";
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

    public void enviar_albuns() {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            String aux = mensagem.get("username") + ";type|album_list;item_count|" + Integer.toString(albuns.size()) + ";";
            for (int i = 0; i < albuns.size(); i++) {
                aux = aux + "item_" + Integer.toString(i) + "_name|" + albuns.get(i).getNome();
                if (i != albuns.size()) {
                    aux = aux + ";";
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

    public void enviar_musicas() {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            String aux = mensagem.get("username") + ";type|music_list;item_count|" + Integer.toString(musicas.size()) + ";";
            for (int i = 0; i < musicas.size(); i++) {
                aux = aux + "item_" + Integer.toString(i) + "_name|" + musicas.get(i).getNome();
                if (i != musicas.size()) {
                    aux = aux + ";";
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

    /* Função que recebe um username e devolve o tipo de permissoes do utilizador */
    public String tipoUser(String utilizador) {
        for (User u : users) {
            if (u.getUsername().equals(utilizador)) {
                return u.getUsertype();
            }
        }
        return null;
    }
    /*
    public void inserir_album() {
        // Vai verificar se o album já existe na base de dados
        // Se já existe
        if (verifica_album(mensagem.get("album_name")) == true) {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_album_try|failed;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se ainda não existe
        else {
            // Criar o novo album
            Data d;
            String[] as;
            as = mensagem.get("album_data").split("/");
            d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
            Album novo;
            ArrayList<Musica> musicas_album = new ArrayList<>();
            ArrayList<String> pessoas_descricoes = new ArrayList<>();
            ArrayList<Critica> criticas = new ArrayList();
            novo = new Album(mensagem.get("album_name"), d, mensagem.get("album_autor"), musicas_album, pessoas_descricoes,criticas);
            // Vou verificar se o Musico já existe
            // Se não existir o Musico
            if (verifica_Musico(mensagem.get("album_autor")) == false) {
                //Vou adicionar o Musico
                ArrayList<Album> lista_albuns = new ArrayList<>();
                Musico a = new Musico(mensagem.get("album_autor"), lista_albuns);
                //Criar a lista de albuns


                Musicos.add(a);
            }
            albuns.add(novo);
            // Adicionar o album à lista de albuns do Musico
            for (Musico a : Musicos) {
                if (a.getNome().equals(mensagem.get("album_autor"))) {// Quando encontrar o Musico
                    //Adicionar o album ao Musico
                    a.getListaAlbuns().add(novo);
                }
            }
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_album_try|sucess;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    */

    /* Funções para gerir os detalhes de cada lista */
    /* Só os editores e o admin é que o podem fazer */
    /* O acesso a estas funções já está protegido pelo RMI Client */
    /*
    public void inserir_Musico() {
        // Vai verificar se o Musico já existe na base de dados
        // Se já existe
        if (verifica_Musico(mensagem.get("Musico_name")) == true) {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_Musico_try|failed;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se não existir, vai criar
        else {
            // Criar o novo Musico
            Data d;
            String[] as;
            as = mensagem.get("Musico_data").split("/");
            d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
            Musico novo;
            ArrayList<Album> lista_albuns = new ArrayList<>();
            novo = new Musico(mensagem.get("Musico_name"), d, mensagem.get("Musico_descricao"), mensagem.get("Musico_genero"), lista_albuns);
            // Adicionar à lista
            Musicos.add(novo);
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_Musico_try|sucess;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    */

    public void inserir_musica() {
        // Vai verificar se a musica já existe na base de dados
        // Se já existe
        if (verifica_musica(mensagem.get("musica_name")) == true) {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_musica_try|failed;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se não existir, vai criar
        else {
            // Criar a nova musica
            Data d;
            String[] as;
            as = mensagem.get("musica_data").split("/");
            d = new Data(Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2]));
            Musica novo;
            novo = new Musica(mensagem.get("musica_name"), d, mensagem.get("musica_compositor"), mensagem.get("musica_autor"), mensagem.get("musica_descricao"), mensagem.get("musica_album"));
            // Vou verificar se o Album e o Musico já existem
            // Se não existir o Musico
            if (verifica_Musico(mensagem.get("musica_autor")) == false) {
                //Vou adicionar o Musico
                Musico a = new Musico(mensagem.get("musica_autor"));
                Musicos.add(a);
            }
            if (verifica_album(mensagem.get("musica_album")) == false) {
                //Vou adicionar o album
                Album a = new Album(mensagem.get("musica_album"));
                albuns.add(a);
            }
            //Procurar o album na lista de albuns e adicionar a musica
            for (Album a : albuns) {
                if (a.getNome().equals(mensagem.get("musica_album"))) {// Quando encontrar o album
                    //Adicionar a musica ao album
                    a.getMusicas().add(novo);
                }
            }
            musicas.add(novo);
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";insere_musica_try|sucess;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remover_Musico() {
        //Verificar se o Musico está na lista
        if (verifica_Musico(mensagem.get("Musico_name")) == true) {
            Iterator<Musico> it = Musicos.iterator();// Cria o iterador
            while (it.hasNext()) {
                Musico a = it.next();
                if (a.getNome().equals(mensagem.get("Musico_name"))) {
                    Musicos.remove(a);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String aux = mensagem.get("username") + ";remove_Musico_try|sucess;" + "type|warning" + ";ID|" + mensagem.get("ID");
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
        } else {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";remove_Musico_try|failed;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remover_album() {
        //Verificar se o album está na lista
        if (verifica_album(mensagem.get("album_name")) == true) {
            Iterator<Album> it = albuns.iterator();// Cria o iterador
            while (it.hasNext()) {
                Album a = it.next();
                if (a.getNome().equals(mensagem.get("album_name"))) {
                    albuns.remove(a);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String aux = mensagem.get("username") + ";remove_album_try|sucess;" + "type|warning" + ";ID|" + mensagem.get("ID");
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
        } else {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + "remove_album_try|failed;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remover_musica() {
        //Verificar se o album está na lista
        if (verifica_musica(mensagem.get("musica_name")) == true) {
            Iterator<Musica> it = musicas.iterator();// Cria o iterador
            while (it.hasNext()) {
                Musica m = it.next();
                if (m.getNome().equals(mensagem.get("musica_name"))) {
                    musicas.remove(m);
                    try {
                        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                        String aux = mensagem.get("username") + "remove_musica_try|sucess;" + "type|warning" + ";ID|" + mensagem.get("ID");
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
        } else {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + "remove_musica_try|failed;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /* Função que verifica se o Musico já se encontra na base de dados */
    // Recebe o nome do Musico, envia true se existir na lista */
    public boolean verifica_Musico(String nome_Musico) {
        if (Musicos.isEmpty()) {
            return false;
        }
        for (Musico a : Musicos) {
            if (a.getNome().equals(nome_Musico))
                return true;
        }
        return false;
    }

    /* Função que verifica se o album já se encontra na base de dados */
    // Recebe o nome do album, envia true se existir na lista */
    public boolean verifica_album(String nome_album) {
        for (Album a : albuns) {
            if (a.getNome().equals(nome_album))
                return true;
        }
        return false;
    }

    /* Função que verifica se a musica já se encontra na base de dados */
    // Recebe o nome do Musico, envia true se existir na lista */
    public boolean verifica_musica(String nome_musica) {
        for (Musica m : musicas) {
            if (m.getNome().equals(nome_musica))
                return true;
        }
        return false;
    }


    public void check_permissions_gerir() {
        // Vai verificar as permissoes para entrar na opção de gerir
        // Se tiver permissoes
        if (tipoUser(mensagem.get("username")).equals("editor") || tipoUser(mensagem.get("username")).equals("admin")) {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";acess|granted;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Se não tiver permissoes
        else {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String aux = mensagem.get("username") + ";acess|denied;" + "type|warning" + ";ID|" + mensagem.get("ID");
                String mensagem = "username|" + aux;
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Metodo que vai dar permissoes a outros utilizadores
    public void make_editor() {
        int flag=0;
        String aux;
        // Vai verificar se o user em questao tem permissao de admin ou utilizador
        // Nao tem permissao, enviar mensagem
        if (tipoUser(mensagem.get("username")).equals("normal")) {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                aux = ";ID|" + mensagem.get("ID");
                String mensagem = "acess|denied;msg|Nao tem permissoes" + aux;
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
                    flag=1;
                    if (u.getUsertype().equals("editor") || u.getUsertype().equals("admin")) {//se já tem permissoes
                        try {
                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                            aux = ";ID|" + mensagem.get("ID");
                            String mensagem = "msg|O utilizador já tem permissões!" + aux;
                            byte[] buffer = mensagem.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {//se ainda não tem permissões
                        try {
                            u.setUsertype("editor");//altera as permissoes
                            aux = "editor_made|" + mensagem.get("editor_name") + ";ID|" + mensagem.get("ID");
                            ;
                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                            String mensagem = "msg|Permissoes atualizadas!;" + aux;
                            byte[] buffer = mensagem.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            //se não encontrou o nome
            if(flag==0) {
                try {
                    aux = ";ID|" + mensagem.get("ID");
                    InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                    String mensagem = "msg|O utilizador não existe!" + aux;
                    byte[] buffer = mensagem.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void criticar_album2() {
        //Vai receber o album que o utilizador quer criticar, a sua mensagem e a cotacao
        ArrayList<Critica> criticas;
        for (Album a : albuns) {
            if (a.getNome().equals(mensagem.get("album_name"))) {//Quando encontra o album na lista de albuns
                Critica c = new Critica(mensagem.get("review_critica"), Integer.parseInt(mensagem.get("review_pontuacao")));
                criticas = a.getCriticas();
                criticas.add(c);
                a.setCriticas(criticas);
            }
        }
        try {
            String aux = "type|warning;msg|Critica escrita com sucesso!"+";ID|" + mensagem.get("ID");;
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            byte[] buffer = aux.getBytes();
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

}

/* Thread que envia o seu id de x em x tempo para o RMI server */
class Helper extends Thread{
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4322;
    private MulticastSocket socket;
    private int server_id;

    // Tem de receber o id do servidor
    Helper(int server_id,MulticastSocket socket){
        this.server_id=server_id;
        this.socket=socket;
    }
    public void run(){
        while(true) {
            try {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                String mensagem = "type|id_warning;idserver|"+Integer.toString(server_id);
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                sleep(10000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}