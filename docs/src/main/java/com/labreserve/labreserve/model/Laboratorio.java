package com.labreserve.labreserve.model;

public class Laboratorio {
    private int idLaboratorio; // Nome igual ao diagrama
    private String nome;
    private int capacidade;
    private String recursos;

    public Laboratorio(int idLaboratorio, String nome, int capacidade, String recursos) {
        this.idLaboratorio = idLaboratorio;
        this.nome = nome;
        this.capacidade = capacidade;
        this.recursos = recursos;
    }

    public int getIdLaboratorio() { return idLaboratorio; }
    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public String getRecursos() { return recursos; }

    @Override
    public String toString() {
        return nome;
    }
}