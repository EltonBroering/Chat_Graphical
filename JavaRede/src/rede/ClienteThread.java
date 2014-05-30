package rede;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import poo.Principal;

public class ClienteThread extends Thread {

    private ObjectOutputStream saida;
    private ObjectInputStream entrada;
    private String enderecoIPServidor;
    private int portaServidor;
    private String nomeParticipante;
    private Principal pai;

    public ClienteThread(Principal p, String nome, String endIP, int porta) {
        this.nomeParticipante = nome;
        this.enderecoIPServidor = endIP;
        this.portaServidor = porta;
        this.pai = p;

    }

    public void enviaMensagem(String mensagem) {
        try {
            //escrevendo na saida da conexao
            saida.writeObject(mensagem);
        } catch (Exception e) {
            System.err.println("Erro ao enviar msg: " + e.toString());
        }
    }

    @Override
    public void run() {
        Socket conexao;

        try {
            conexao = new Socket(enderecoIPServidor, portaServidor);

            System.out.println("Conectado no servidor...");

            //obtem os fluxos de entrada e saida desta conexao
            this.saida = new ObjectOutputStream(conexao.getOutputStream());
            this.entrada = new ObjectInputStream(conexao.getInputStream());
            boolean conectado = true;

            
            // ao conectar, envie uma mensagem informando o nome do Participante
            saida.writeObject(this.nomeParticipante);

            
            // enquanto a conexao estiver ativa, veja o que vem pela rede
            // e escreva na janela do bate papo
            while (conectado) {
                String msgRecebida = (String) entrada.readObject();

                // se vier a mensagem com o texto "SairDaSala", desconecte e
                // e escreva "desconectou"
                if (msgRecebida.equals("SairDaSala")) {
                    conectado = false;
                    msgRecebida = "desconectou";
                    this.pai.jBConectar.setText("Conectar");
                }

                // escreva na tela do usuario
                this.pai.adicionarMensagem(msgRecebida);
            }
            
            // fechando os fluxos
            saida.close();
            entrada.close();
            conexao.close();

        } catch (Exception e) {
            System.err.println("Erro na conexao: " + e.toString());
            this.pai.jBConectar.setText("Conectar");
        }

    }

}
