package rede;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Um objeto desta classe é criado para cada cliente que se conectar no servidor
 * Assim, o servidor terá um thread para atender cada cliente de forma separada
 *
 */
public class ServidorThread extends Thread {

    private Socket conexao;

    // Lista com os fluxos de saida de todos os clientes conectados no servidor
    // Esta lista é compartilhada por todos objetos da classe ServidorThread
    private ArrayList<ObjectOutputStream> saidaDosParticipantes;
    private boolean conectado;

    public ServidorThread(Socket c, ArrayList<ObjectOutputStream> aL) {
        this.conexao = c;
        this.saidaDosParticipantes = aL;
        this.conectado = false;
    }

    @Override
    public void run() {
        ObjectOutputStream saida;
        ObjectInputStream entrada;
        try {
            //obtendo os fluxos de entrada e de saida
            saida = new ObjectOutputStream(conexao.getOutputStream());
            entrada = new ObjectInputStream(conexao.getInputStream());

            // adicionando no ArrayList o fluxo de saida deste cliente que 
            // acabou de conectar
            // esse ArrayList é compartilhado por todas as Threads do ServidorThread
            this.saidaDosParticipantes.add(saida);

            this.conectado = true;
            saida.writeObject("bem vindo ao servidor");

            // Regra deste exemplo: O cliente ao conectar sempre irá enviar seu nome. 
            String nome = (String) entrada.readObject();

            // Ficará neste laço esperando todas as mensagens que o cliente enviar
            while (conectado) {

                // Fica travado na linha abaixo até receber uma mensagem do cliente
                String mensagemRecebida = (String) entrada.readObject();

                // As linhas abaixo só serão executadas depois que o cliente
                // enviar uma mensagem
                // A mensagem enviada é o código secreto para sair da sala?
                // Se sim, envie para os demais clientes a mensagem
                // "uma pessoa saiu da sala"
                // e para quem saiu, envie o código secreto "SairDaSala"
                if (mensagemRecebida.equals("SairDaSala")) {
                    conectado = false;
                    saida.writeObject(mensagemRecebida);
                    this.saidaDosParticipantes.remove(saida);
                } else {
                // Envie a mensagem recebida para todos os outros cliente,
                    // mas não envie para o cliente que originou esta mensagem
                    for (ObjectOutputStream clientes : saidaDosParticipantes) {
                        // Garanta que não irá enviar para o cliente que originou a mensagem
                        if (clientes != saida) {
                            clientes.writeObject(mensagemRecebida);
                        }

                    }
                }

            } // fim do while do conectado

            // removendo o fluxo de saida deste Cliente
            // fechando os fluxos e a conexao
            saida.close();
            entrada.close();
            conexao.close();
        } catch (Exception ex) {
            System.err.println("Erro no ServidorThread: " + ex.toString());
        }
    }

}
