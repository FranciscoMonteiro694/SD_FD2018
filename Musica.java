import java.io.Serializable;

public class Musica implements Serializable {
    private String nome;
    private String compositor;
    private Data data_lancamento;
    private String descricao;

    Musica(String nome,Data data_lancamento,String compositor,String descricao){
        this.nome=nome;
        this.data_lancamento=data_lancamento;
        this.compositor=compositor;
        this.descricao=descricao;


    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
