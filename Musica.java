import java.io.Serializable;

public class Musica implements Serializable {
    private String nome;
    private String autor;
    private String compositor;
    private Data data_lancamento;
    private String descricao;
    private String album;

    Musica(String nome,Data data_lancamento,String compositor,String autor,String descricao,String album){
        this.nome=nome;
        this.data_lancamento=data_lancamento;
        this.compositor=compositor;
        this.autor=autor;
        this.descricao=descricao;
        this.album=album;


    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCompositor() {
        return compositor;
    }

    public void setCompositor(String compositor) {
        this.compositor = compositor;
    }

    public Data getData_lancamento() {
        return data_lancamento;
    }

    public void setData_lancamento(Data data_lancamento) {
        this.data_lancamento = data_lancamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }



}
