/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

//saopaulo:qte:idT:a
//rio:qte:idT:r
//rio:qte
public class InterfaceClienteImpl extends UnicastRemoteObject implements InterfaceCliente {
    
    String nome;
    File db;
    List<String> minhaColecao;
    File dbTmp;
    
    InterfaceClienteImpl(InterfaceServidor referenciaServidor) throws RemoteException {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Digite o nome do cliente:");
            this.nome = bufferRead.readLine();
        } catch (IOException ex) {
            Logger.getLogger(InterfaceClienteImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        referenciaServidor.sayHello(nome, this);

        //criando ARQUIVO DE CARTAS
        db = LogHelper.generateCardCollection(nome);
        //minhaColecao = LogHelper.readdb(db);
        //criando db temporaria
        dbTmp = LogHelper.createF(nome + "Tmp");
        LogHelper.copyFile(db, dbTmp);

        //colocar interface em um loop aqui
        while (true) {
            System.out.println("1 - buscar cartas de todos os colecionadores");
            System.out.println("2 - trocar cartas");
            
            Scanner keyboard = new Scanner(System.in);
            System.out.println("enter an integer");
            String cmd = keyboard.nextLine();
            
            if (cmd.equals("1")) {
                String resposta = referenciaServidor.listAllCards();
                System.out.println("cartas encontradas:\n" + resposta);
            }
            if (cmd.equals("2")) {
                Scanner k = new Scanner(System.in);
                System.out.println("Com quem quer trocar");
                String outraPessoa = k.nextLine();
                System.out.println("voce oferece qual carta?");
                String minha = k.nextLine();
                System.out.println("voce quer qual carta?");
                String quero = k.nextLine();
                
                Card offered = new Card(minha, 1);
                Card wanted = new Card(quero, 1);
                
                String resposta = referenciaServidor.startTransaction(nome, this, offered, wanted, outraPessoa);
            }
        }
    }

    //não utilizada
    @Override
    public String echo(String texto) throws RemoteException {
        System.out.println("Recebi>" + texto);
        return null;
    }

    //não utilizada
    @Override
    public String insertCard(Card card) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //não utilizada
    @Override
    public String removeCard(Card card) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List getCards() throws RemoteException {
        minhaColecao = LogHelper.readdb(db);
        return minhaColecao;
    }
    
    @Override
    public String finishTransaction(int idT) throws RemoteException {
        //rio:qte:idT:r
        //procura no dpTmp as linhas que possuem dados temporarios
        List<String> dadosDbTmp = LogHelper.readdb(dbTmp);
        
        for (String l : dadosDbTmp) {
            String[] partes = l.trim().split(":");
            if (partes.length > 2) {
                int idLido = Integer.parseInt(partes[2]);
                if (idLido == idT) {
                    String acao = partes[3];
                    //faz a operacao no db final
                    if (acao.equalsIgnoreCase("r")) {
                        LogHelper.retirarCartao(db, partes[0]);
                    } else if (acao.equalsIgnoreCase("a")) {
                        LogHelper.colocarCartao(db, partes[0]);
                    }
                    //retira essa transação do dbTmp
                    l = partes[0] + LogHelper.lerQtde(db, partes[0]);
                }
            }
        }
        LogHelper.recreateFile(db, dadosDbTmp);
        
        return "";
    }
    
    @Override
    public String abortTransaction(int idT) throws RemoteException {
        //apaga os indicadores de operação do dpTmp
        List<String> dadosDbTmp = LogHelper.readdb(dbTmp);
        
        for (String l : dadosDbTmp) {
            String[] partes = l.trim().split(":");
            if (partes.length > 2) {
                int idLido = Integer.parseInt(partes[2]);
                if (idLido == idT) {
                    //retira essa transação do dbTmp
                    l = partes[0] + partes[1];
                }
            }
        }
        LogHelper.recreateFile(db, dadosDbTmp);
        return "";
    }
    
    @Override
    public String trocarCartao(int idT, Card aRetirar, Card aReceber) throws RemoteException {
        //coloca as mudanças no aquivo temporario
        LogHelper.retirarCartaoTmp(dbTmp, aRetirar.nome, idT);
        LogHelper.colocarCartaoTmp(dbTmp, aReceber.nome, idT);
        return "";
    }
    
}
