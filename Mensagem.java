import java.util.HashMap;

public class Mensagem implements java.io.Serializable {
    private HashMap<String, String> map;

    Mensagem(HashMap<String, String> map) {
        this.map = map;
    }
}
