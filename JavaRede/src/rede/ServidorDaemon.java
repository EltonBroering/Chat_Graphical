package rede;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 *
 * Esta classe é responsável por criar um Thread de Servidor para cada cliente
 * que se conectar. 
 * 
 */
public class ServidorDaemon extends Thread {

   
    private int porta; // porta onde ficará ouvindo
    private ServerSocket servidor; // socket TCP
    // lista com os fluxos de saida de todos os clientes conectados. 
    // Isso é compartilhado por todos os objetos da classe ServidorThread
    private ArrayList<ObjectOutputStream> saidaDosParticipantes;
    
    private boolean sair;

    /**
     * Para criar um servidorDaemon é necessário informar a porta onde este
     * ficará ouvindo (esperando por conexões)
     *
     * @param po porta
     */
    public ServidorDaemon(int po) {
        this.porta = po;
   
        this.saidaDosParticipantes = new ArrayList<ObjectOutputStream>();
        this.sair = false;
    }

    /**
     * Método que será executado quando alguém invocar o método start() desta
     * classe
     */
    @Override
    public void run() {
        try {
            // Criando socket TCP para ouvir na porta
            this.servidor = new ServerSocket(this.porta, 10);

            // Ficará neste laço até que o usuário diga para não mais ouvir
            while (!sair) {
                
                // fica esperando um cliente conectar.
                Socket conexao = servidor.accept();
                // As linhas abaixo só serão executadas se um cliente se conectar.
                // Ou seja, a execução fica travada na linha acima até o cliente conectar
                

                // cliente conectou, crie um thread só para ele
                System.out.println("O seguinte cliente conectou: " + conexao.getInetAddress().getHostAddress());

                Thread t = new ServidorThread(conexao, this.saidaDosParticipantes);
                t.start();
            }
        } catch (SocketException e) {
            System.out.println("Servidor foi desligado");

        } catch (Exception e) {
            System.err.println("Erro: " + e.toString());
        }
    }

    /**
     * Para fazer o servidor parar de ouvir na porta. Finalizando o processo
     *
     */
    public void parar() {
        try {
            this.sair = true;
        
            
            this.servidor.close();
        } catch (Exception ex) {
            System.err.println("Erro no parar: " + ex.toString());
        }
    }

}
