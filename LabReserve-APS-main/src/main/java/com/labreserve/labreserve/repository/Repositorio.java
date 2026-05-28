package com.labreserve.labreserve.repository;

import com.labreserve.labreserve.model.Laboratorio;
import com.labreserve.labreserve.model.Reserva;
import com.labreserve.labreserve.model.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Repositorio {
    public static List<Usuario> usuarios = new ArrayList<>();
    public static List<Laboratorio> laboratorios = new ArrayList<>();
    public static List<Reserva> reservas = new ArrayList<>();

    private static int proximoIdReserva = 1;
    private static int proximoIdUsuario = 5;
    private static int proximoIdLab = 6;

    static {
        // --- UTILIZADORES DO IFPB (Agora com Email, Palavra-passe e Tipo corretos do Documento) ---
        usuarios.add(new Usuario(1, "Prof. Tuca", "Admin", "tuca@ifpb.edu.br", "123456"));
        usuarios.add(new Usuario(2, "Prof. Alvaro", "Admin", "alvaro@ifpb.edu.br", "123456"));
        usuarios.add(new Usuario(3, "Pedro", "Comum", "pedro@aluno.ifpb.edu.br", "123"));
        usuarios.add(new Usuario(4, "Denilson", "Comum", "denilson@aluno.ifpb.edu.br", "123"));

        // --- LABORATÓRIOS DO IFPB ---
        laboratorios.add(new Laboratorio(1, "Lab. Química", 20, "Bancadas, Reagentes, Capela de Exaustão"));
        laboratorios.add(new Laboratorio(2, "Lab. Info 1", 30, "30 PCs, Projetor, Ar-condicionado"));
        laboratorios.add(new Laboratorio(3, "Lab. Info 2", 30, "30 PCs, Linux, Ar-condicionado"));
        laboratorios.add(new Laboratorio(4, "Lab. Info 3", 25, "25 PCs, Windows 11, Quadro Branco"));
        laboratorios.add(new Laboratorio(5, "Lab. Info 4", 25, "25 PCs, Internet Fibra, Ar-condicionado"));
    }

    // Novo Método: Busca o utilizador pelo email e valida a palavra-passe
    public static Usuario realizarLogin(String email, String senha) {
        for (Usuario u : usuarios) {
            if (u.fazerLogin(email, senha)) {
                return u;
            }
        }
        return null;
    }

    // RF04 - Verificação de Disponibilidade com LocalTime (Lógica matemática de conflito)
    public static boolean verificarDisponibilidade(Laboratorio lab, LocalDate data, LocalTime horarioInicio, LocalTime horarioFim) {
        for (Reserva r : reservas) {
            if (r.getLaboratorio().getIdLaboratorio() == lab.getIdLaboratorio() && r.getData().equals(data)) {
                // Lógica de intersecção de tempo:
                // Se o início desejado for ANTES do fim da reserva existente
                // E o fim desejado for DEPOIS do início da reserva existente -> HÁ CONFLITO!
                if (horarioInicio.isBefore(r.getHorarioFim()) && horarioFim.isAfter(r.getHorarioInicio())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void salvarReserva(Usuario usuario, Laboratorio lab, LocalDate data, LocalTime inicio, LocalTime fim) {
        Reserva nova = new Reserva(proximoIdReserva++, usuario, lab, data, inicio, fim, "Pendente");
        reservas.add(nova);
    }

    public static void cadastrarUsuario(String nome, String tipo, String email, String senha) {
        usuarios.add(new Usuario(proximoIdUsuario++, nome, tipo, email, senha));
    }

    public static void cadastrarLaboratorio(String nome, int capacidade, String recursos) {
        laboratorios.add(new Laboratorio(proximoIdLab++, nome, capacidade, recursos));
    }

    public static List<Reserva> obterReservasDoDia() {
        LocalDate hoje = LocalDate.now();
        return reservas.stream()
                .filter(r -> r.getData().equals(hoje))
                .collect(Collectors.toList());
    }
}