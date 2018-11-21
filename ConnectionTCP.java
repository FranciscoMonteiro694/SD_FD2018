import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


//Thread para tratar do TCP

public class ConnectionTCP extends Thread {
    Socket clientSocket;
    ServerSocket server;
    InputStream in;
    OutputStream out;
    String filename;
    int aux;
    byte [] buffer =new byte[1024];

    public ConnectionTCP(ServerSocket server, String filename) {
        this.server=server;
        this.filename=filename;
    }
    //=============================
    public void run(){
        String resposta;
        try{
            while(true){
                Socket clientSocket = server.accept(); // BLOQUEANTE ou seja vai tar sempre aqui nesta condicao ate que seja aceite uma nova conexao apos isto acaba o ciclo e fica outra vez aqui a espera
                System.out.println("Ligação aceite="+clientSocket);
                in = clientSocket.getInputStream();//vai ser o que o cliente escreve ou seja o que o server recebe e mete em capslock
                //in.read(buffer);
                filename+=".mp3";
                out = new FileOutputStream(filename);
                while((aux = in.read(buffer))!=-1) {
                    out.write(buffer, 0, aux);
                }
                out.close();//Fechar o file
            }
        }catch(EOFException e){System.out.println("EOF:" + e);
        }catch(IOException e){System.out.println("IO:" + e);}
    }
}
