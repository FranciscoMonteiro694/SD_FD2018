import java.rmi.*;
import java.util.ArrayList;

public interface Hello_S_I extends Remote {
	public void print_on_server(String s) throws java.rmi.RemoteException;
  	public void subscribe(String name, Hello_C_I client) throws RemoteException;
  	public boolean register(String name,String password,Hello_C_I c)throws RemoteException;
        public boolean login(String name,String password,Hello_C_I c)throws RemoteException;
        public boolean is_Editor(String name,Hello_C_I c)throws RemoteException;
        public boolean insere_Artista(String name,String genero,String descricao,String data,String username,Hello_C_I c)throws RemoteException;
        public boolean remove_Artista(String name,String username,Hello_C_I c)throws RemoteException;
        public boolean insere_Album(String username,String nome,String musicas,String descricao,String autor,String data,Hello_C_I c)throws RemoteException;
        public boolean remove_Album(String name,String username,Hello_C_I c)throws RemoteException;
        public boolean insere_Musica(String username,String name,String autor,String compositor,String data,String album,String descricao,Hello_C_I c)throws RemoteException;
        public boolean remove_Musica(String name,String username,Hello_C_I c)throws RemoteException;
        public void Pesquisa_Geral(ArrayList<String> Albuns,ArrayList<String> Musicas ,ArrayList<String> Artistas,String pesq,String username,Hello_C_I c)throws RemoteException;
        public void Pesquisa_Musica(String pesq,String username,Hello_C_I c)throws RemoteException;
        public void Pesquisa_Album(String pesq,ArrayList<String> Musicas,String username,Hello_C_I c)throws RemoteException;
        public void Pesquisa_Artista(String pesq,ArrayList<String> Albuns,String username,Hello_C_I c)throws RemoteException;
        //public void PesquisaMusicaAlbum(String album,Hello_C_I c)throws RemoteException;
        //public void PesquisaMusicaArtista(String artista,Hello_C_I c)throws RemoteException;
        //public voidConsultaAlbum(String album,Hello_C_I c)throws RemoteException;
        //public voidConsultaArtista(String artista,Hello_C_I c)throws RemoteException;
}