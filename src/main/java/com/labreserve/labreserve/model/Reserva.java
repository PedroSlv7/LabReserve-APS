package com.labreserve.labreserve.model;

import java.time.LocalDate;

public class Reserva {
    private int id;
    private Usuario usuario;
    private Laboratorio laboratorio;
    private LocalDate data;
    private String horario; // Ex: "08:00 - 10:00"
    private String status;  // "Pendente", "Confirmada", "Em Uso", "Cancelada"

    public Reserva(int id, Usuario usuario, Laboratorio laboratorio, LocalDate data, String horario, String status) {
        this.id = id;
        this.usuario = usuario;
        this.laboratorio = laboratorio;
        this.data = data;
        this.horario = horario;
        this.status = status;
    }

    public Usuario getUsuario() { return usuario; }
    public Laboratorio getLaboratorio() { return laboratorio; }
    public LocalDate getData() { return data; }
    public String getHorario() { return horario; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }
}