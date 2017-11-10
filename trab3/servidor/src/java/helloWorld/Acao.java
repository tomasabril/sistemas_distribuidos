package helloWorld;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 * @author a1013343
 */
public class Acao {

    public String nome;
    public float precoDeMercado;
    //.............\/dentro desse hashmap tem <idCliente, preco>
    public List<DataCliente> compradores;
    public List<DataCliente> vendedores;

    public Acao(String nome) {
        this.nome = nome;
        compradores = new ArrayList<>();
        vendedores = new ArrayList<>();
        //para gerar um valor inicial para a ação entre 10 e 100
        precoDeMercado = (float) (10 + Math.random() * (100 - 10));
    }

    //recebe onovo preço e notifica a todos os interessados dessa ação
    public void mudaPreco(float novoPreco) {
        this.precoDeMercado = novoPreco;
    }
}
