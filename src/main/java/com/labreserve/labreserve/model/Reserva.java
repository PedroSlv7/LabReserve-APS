package com.labreserve.labreserve.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private int idReserva; // Nome igual ao diagrama
    private Usuario usuario;
    private Laboratorio laboratorio;
    private LocalDate data;
    private LocalTime horarioInicio; // Novo atributo do diagrama
    private LocalTime horarioFim;    // Novo atributo do diagrama
    private String status;  // "Pendente", "Confirmada", "Em Uso", "Cancelada"

    public Reserva(int idReserva, Usuario usuario, Laboratorio laboratorio, LocalDate data, LocalTime horarioInicio, LocalTime horarioFim, String status) {
        this.idReserva = idReserva;
        this.usuario = usuario;
        this.laboratorio = laboratorio;
        this.data = data;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.status = status;
    }

    public int getIdReserva() { return idReserva; }
    public Usuario getUsuario() { return usuario; }
    public Laboratorio getLaboratorio() { return laboratorio; }
    public LocalDate getData() { return data; }
    public LocalTime getHorarioInicio() { return horarioInicio; }
    public LocalTime getHorarioFim() { return horarioFim; }
    public String getStatus() { return status; }

    // Métodos exigidos no diagrama de classes
    public void solicitarReserva() {
        this.status = "Pendente";
    }

    public void cancelarReserva() {
        this.status = "Cancelada";
    }

    public void atualizarStatus(String novoStatus) {
        this.status = novoStatus;
    }
}