package com.labreserve.labreserve.model;

public class Laboratorio {
    private int id;
    private String nome;
    private int capacidade;
    private String recursos;

    public Laboratorio(int id, String nome, int capacidade, String recursos) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.recursos = recursos;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome; // Para aparecer o nome bonitinho na seleção da tela
    }
}