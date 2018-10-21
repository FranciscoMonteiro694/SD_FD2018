import java.util.ArrayList;

public class Playlist {
    private ArrayList<Musica> musicas;
    private String permissao;//Privado ou publica

    Playlist(ArrayList<Musica> musicas,String permissao){
        this.musicas=musicas;
        this.permissao=permissao;
    }

    public ArrayList<Musica> getMusicas() {
        return musicas;
    }

    public void setMusicas(ArrayList<Musica> musicas) {
        this.musicas = musicas;
    }

    public String getPermissao() {
        return permissao;
    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }
}
