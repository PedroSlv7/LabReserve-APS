package com.labreserve.labreserve.model;

public class Usuario {
    private int id;
    private String nome;
    private String identificacao;
    private String tipo; // "Professor" ou "Aluno"

    public Usuario(int id, String nome, String identificacao, String tipo) {
        this.id = id;
        this.nome = nome;
        this.identificacao = identificacao;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return nome + " (" + tipo + ")"; // Facilita a exibição na tela
    }
}