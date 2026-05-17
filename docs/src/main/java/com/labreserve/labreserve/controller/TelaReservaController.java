package com.labreserve.labreserve.controller;

import com.labreserve.labreserve.model.Laboratorio;
import com.labreserve.labreserve.model.Reserva;
import com.labreserve.labreserve.model.Usuario;
import com.labreserve.labreserve.repository.Repositorio;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class TelaReservaController {

    private Usuario usuarioLogado;
    private ComboBox<Laboratorio> cbLaboratorio;
    private DatePicker dpData;
    private ComboBox<String> cbHorario;
    private TableView<Reserva> tvReservas;

    public TelaReservaController(Usuario usuarioLogado, ComboBox<Laboratorio> cbLaboratorio,
                                 DatePicker dpData, ComboBox<String> cbHorario, TableView<Reserva> tvReservas) {
        this.usuarioLogado = usuarioLogado;
        this.cbLaboratorio = cbLaboratorio;
        this.dpData = dpData;
        this.cbHorario = cbHorario;
        this.tvReservas = tvReservas;
    }

    public void inicializarDados() {
        atualizarComponentesSelecao();
        atualizarTabela(false);
    }

    public void atualizarComponentesSelecao() {
        cbLaboratorio.setItems(FXCollections.observableArrayList(Repositorio.laboratorios));

        cbHorario.setItems(FXCollections.observableArrayList(
                "07:30 - 09:30",
                "09:30 - 11:30",
                "07:30 - 11:30",
                "13:30 - 15:30",
                "15:30 - 17:30",
                "13:30 - 17:30"
        ));
    }

    public void acaoSolicitarReserva() {
        Laboratorio labSel = cbLaboratorio.getValue();
        LocalDate dataSel = dpData.getValue();
        String horarioSel = cbHorario.getValue();

        if (labSel == null || dataSel == null || horarioSel == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha todos os campos da reserva.");
            return;
        }

        String[] partes = horarioSel.split(" - ");
        LocalTime inicio = LocalTime.parse(partes[0]);
        LocalTime fim = LocalTime.parse(partes[1]);

        boolean disponivel = Repositorio.verificarDisponibilidade(labSel, dataSel, inicio, fim);

        if (disponivel) {
            Repositorio.salvarReserva(usuarioLogado, labSel, dataSel, inicio, fim);
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Reserva efetuada! Status inicial: Pendente.");
            atualizarTabela(false);
            limparFormularioReserva();
        } else {
            exibirAlerta(Alert.AlertType.ERROR, "Conflito", "Esse laboratório já está reservado neste horário!");
        }
    }

    // NOVO MÉTODO: Cancelar Reserva com verificação de segurança
    public void acaoCancelarReserva(Reserva selecionada) {
        if (selecionada == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione uma reserva na tabela para cancelar.");
            return;
        }

        // Verifica se é o dono da reserva ou se é Administrador
        if (usuarioLogado.getTipo().equals("Admin") || selecionada.getUsuario().getIdUsuario() == usuarioLogado.getIdUsuario()) {
            Repositorio.reservas.remove(selecionada);
            exibirAlerta(Alert.AlertType.INFORMATION, "Cancelada", "A reserva foi cancelada e removida com sucesso.");
            atualizarTabela(false);
        } else {
            exibirAlerta(Alert.AlertType.ERROR, "Permissão Negada", "Você só pode cancelar as suas próprias reservas.");
        }
    }

    public void acaoCadastrarUsuario(String nome, String tipo, String email, String senha) {
        if (nome.isEmpty() || tipo == null || email.isEmpty() || senha.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha todos os campos do usuário.");
            return;
        }
        Repositorio.cadastrarUsuario(nome, tipo, email, senha);
        exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário cadastrado com sucesso!");
    }

    public void acaoCadastrarLaboratorio(String nome, String capStr, String recursos) {
        if (nome.isEmpty() || capStr.isEmpty() || recursos.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha todos os campos do laboratório.");
            return;
        }
        try {
            int capacidade = Integer.parseInt(capStr);
            Repositorio.cadastrarLaboratorio(nome, capacidade, recursos);
            atualizarComponentesSelecao();
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Laboratório cadastrado com sucesso!");
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "A capacidade deve ser um número inteiro.");
        }
    }

    public void acaoAlterarStatus(Reserva selecionada, String novoStatus) {
        if (selecionada == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione uma reserva na tabela antes de clicar em atualizar.");
            return;
        }

        if (novoStatus.equals("Finalizada") || novoStatus.equals("Cancelada")) {
            Repositorio.reservas.remove(selecionada);
            exibirAlerta(Alert.AlertType.INFORMATION, "Reserva Concluída", "A reserva foi definida como " + novoStatus + " e removida do painel ativo.");
        } else {
            selecionada.atualizarStatus(novoStatus);
            exibirAlerta(Alert.AlertType.INFORMATION, "Status Atualizado", "Status alterado para: " + novoStatus);
        }

        atualizarTabela(false);
    }

    public void atualizarTabela(boolean apenasHoje) {
        if (apenasHoje) {
            tvReservas.setItems(FXCollections.observableArrayList(Repositorio.obterReservasDoDia()));
        } else {
            tvReservas.setItems(FXCollections.observableArrayList(Repositorio.reservas));
        }
        tvReservas.refresh();
    }

    public void refreshTableHoje(boolean apenasHoje) {
        atualizarTabela(apenasHoje);
    }

    private void limparFormularioReserva() {
        cbLaboratorio.setValue(null);
        dpData.setValue(null);
        cbHorario.setValue(null);
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}