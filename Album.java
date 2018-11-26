import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    private String nome;
    private Data data_lancamento;
    private double pontuacao_med;
    private String descricao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    Album(String nome) {
        this.nome = nome;
        this.pontuacao_med = 0;
    }


    public double getPontuacao_med() {
        return pontuacao_med;
    }

    public void setPontuacao_med(double pontuacao_med) {
        this.pontuacao_med = pontuacao_med;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    Album(String nome, Data data_lancamento) {
        this.data_lancamento = data_lancamento;
        this.nome = nome;
        this.pontuacao_med = 0;
    }

    Album(String nome, Data data_lancamento , String descricao){
        this.nome=nome;
        this.data_lancamento=data_lancamento;
        this.pontuacao_med = 0;
        this.descricao=descricao;
    }
    public Data getData_lancamento() {
        return data_lancamento;
    }

    public void setData_lancamento(Data data_lancamento) {
        this.data_lancamento = data_lancamento;
    }

}
