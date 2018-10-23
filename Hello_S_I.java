import java.rmi.*;

public interface Hello_S_I extends Remote {
	public void print_on_server(String s) throws java.rmi.RemoteException;
  	public void subscribe(String name, Hello_C_I client) throws RemoteException;
  	public boolean register(String name,String password,Hello_C_I c)throws RemoteException;
        public boolean login(String name,String password,Hello_C_I c)throws RemoteException;
        public boolean is_Editor(String name,Hello_C_I c)throws RemoteException;
        public boolean insere_Artista(String name,String genero,String descricao,String data,String username,Hello_C_I c)throws RemoteException;
        public boolean remove_Artista(String name,String username,Hello_C_I c)throws RemoteException;
        public void insere_Album(String name,String username,Hello_C_I c)throws RemoteException;
        public void remove_Album(String name,String username,Hello_C_I c)throws RemoteException;
        //public void PesquisaMusicaAlbum(String album,Hello_C_I c)throws RemoteException;
        //public void PesquisaMusicaArtista(String artista,Hello_C_I c)throws RemoteException;
        //public voidConsultaAlbum(String album,Hello_C_I c)throws RemoteException;
        //public voidConsultaArtista(String artista,Hello_C_I c)throws RemoteException;
}