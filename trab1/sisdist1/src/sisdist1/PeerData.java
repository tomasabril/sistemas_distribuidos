/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sisdist1;

import java.security.PublicKey;
import java.util.ArrayList;

public class PeerData implements Comparable<PeerData> {

    public int port;
    public long timeOfLastPing;
    public PublicKey publicKey;
    public ArrayList<String> produtos;
    public int reputacao;

    //construtor apenas com porta
    public PeerData(int porta) {
        port = porta;
        timeOfLastPing = System.currentTimeMillis();
    }

    //construtor com porta e publickey
    public PeerData(int porta, PublicKey pk) {
        reputacao = 0;
        publicKey = pk;
        port = porta;
        produtos = new ArrayList<>();
        timeOfLastPing = System.currentTimeMillis();
    }

    //checar se o peer esta funcional
    public boolean isAlive() {
        long now = System.currentTimeMillis();
        if (now - timeOfLastPing > 5010) {
            // nao está respondendo
            //System.out.println("Its dead jim");
            return false;
        }
//        System.out.println("not dead yet");
        return true;
    }

    //adiciona os produtos a serem vendidos na lista de produtos
    public void addCmd(String p) {
        String[] partes = p.split("=:=", 0);
        if (partes[1].trim().equals("venda")) {
            produtos.add(partes[2].trim() + ":" + partes[3].trim());
        }
    }

    //faz o update do tempo para o keep alive
    public void updateTime() {
        timeOfLastPing = System.currentTimeMillis();
    }

    @Override
    public int compareTo(PeerData o) {
        return Integer.compare(this.port, o.port);
    }

    @Override
    public String toString() {
        return "{" + port + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.port;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PeerData other = (PeerData) obj;
        if (this.port != other.port) {
            return false;
        }
        return true;
    }

}
