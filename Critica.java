import java.io.Serializable;

public class Critica implements Serializable {
    private String justificao;//ate 300 palavras
    private int avaliacao;//de 0 a 10
    private String user;

    Critica(String justificao,int avaliacao){
        this.justificao=justificao;
        this.avaliacao=avaliacao;
    }

    public String getJustificao() {
        return justificao;
    }

    public void setJustificao(String justificao) {
        this.justificao = justificao;
    }

    public int getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(int avaliacao) {
        this.avaliacao = avaliacao;
    }
}
