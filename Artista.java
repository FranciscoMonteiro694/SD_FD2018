import java.io.Serializable;
import java.util.ArrayList;

public class Artista implements Serializable {
    private String nome;
    private String genero;
    private Data data_criacao;
    private String descricao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Data getData_nasc() {
        return data_criacao;
    }

    public void setData_nasc(Data data_nasc) {
        this.data_criacao = data_criacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    Artista(String nome){
        this.nome=nome;
    }


    Artista(String nome, Data data_criacao, String descricao, String genero){
        this.nome=nome;
        this.descricao=descricao;
        this.data_criacao=data_criacao;
        this.genero=genero;
    }

    public Artista(String nome, String genero, Data data_criacao, String descricao) {
        this.nome = nome;
        this.genero = genero;
        this.data_criacao = data_criacao;
        this.descricao = descricao;
    }

    public Data getData_criacao() {
        return data_criacao;
    }

    public void setData_criacao(Data data_criacao) {
        this.data_criacao = data_criacao;
    }
}
