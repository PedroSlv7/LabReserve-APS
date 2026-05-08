package com.labreserve.labreserve.repository;

import com.labreserve.labreserve.model.Laboratorio;
import com.labreserve.labreserve.model.Reserva;
import com.labreserve.labreserve.model.Usuario;

import java.time.LocalDate;
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
        // --- USUÁRIOS DO IFPB ---
        usuarios.add(new Usuario(1, "Prof. Tuca", "IF-1001", "Professor"));
        usuarios.add(new Usuario(2, "Prof. Alvaro Magnum", "IF-1002", "Professor"));
        usuarios.add(new Usuario(3, "Pedro", "MAT-202401", "Aluno"));
        usuarios.add(new Usuario(4, "Denilson", "MAT-202402", "Aluno"));

        // --- LABORATÓRIOS DO IFPB ---
        laboratorios.add(new Laboratorio(1, "Lab. Química", 20, "Bancadas, Reagentes, Capela de Exaustão"));
        laboratorios.add(new Laboratorio(2, "Lab. Info 1", 30, "30 PCs, Projetor, Ar-condicionado"));
        laboratorios.add(new Laboratorio(3, "Lab. Info 2", 30, "30 PCs, Linux, Ar-condicionado"));
        laboratorios.add(new Laboratorio(4, "Lab. Info 3", 25, "25 PCs, Windows 11, Quadro Branco"));
        laboratorios.add(new Laboratorio(5, "Lab. Info 4", 25, "25 PCs, Internet Fibra, Ar-condicionado"));
    }

    // RF04 - Verificação de Disponibilidade Avançada (Trata conflito de Turno Inteiro vs Frações)
    public static boolean verificarDisponibilidade(Laboratorio lab, LocalDate data, String horarioSolicitado) {
        for (Reserva r : reservas) {
            if (r.getLaboratorio().getNome().equals(lab.getNome()) && r.getData().equals(data)) {
                String hExistente = r.getHorario();

                // 1. Se os horários forem exatamente iguais, há conflito óbvio.
                if (hExistente.equals(horarioSolicitado)) {
                    return false;
                }

                // 2. Se a reserva existente for o Turno Inteiro da Manhã, bloqueia qualquer fração da manhã.
                if (hExistente.contains("Manhã: Turno Inteiro") && horarioSolicitado.contains("Manhã")) {
                    return false;
                }
                // Vice-versa: Se tentar reservar Turno Inteiro da Manhã mas já houver fração da manhã reservada.
                if (horarioSolicitado.contains("Manhã: Turno Inteiro") && hExistente.contains("Manhã")) {
                    return false;
                }

                // 3. Se a reserva existente for o Turno Inteiro da Tarde, bloqueia qualquer fração da tarde.
                if (hExistente.contains("Tarde: Turno Inteiro") && horarioSolicitado.contains("Tarde")) {
                    return false;
                }
                // Vice-versa: Se tentar reservar Turno Inteiro da Tarde mas já houver fração da tarde reservada.
                if (horarioSolicitado.contains("Tarde: Turno Inteiro") && hExistente.contains("Tarde")) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void salvarReserva(Usuario usuario, Laboratorio lab, LocalDate data, String horario) {
        Reserva nova = new Reserva(proximoIdReserva++, usuario, lab, data, horario, "Pendente");
        reservas.add(nova);
    }

    public static void cadastrarUsuario(String nome, String identificacao, String tipo) {
        usuarios.add(new Usuario(proximoIdUsuario++, nome, identificacao, tipo));
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