package com.labreserve.labreserve.controller;

import com.labreserve.labreserve.model.Laboratorio;
import com.labreserve.labreserve.model.Reserva;
import com.labreserve.labreserve.model.Usuario;
import com.labreserve.labreserve.repository.Repositorio;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import java.time.LocalDate;

public class TelaReservaController {

    private ComboBox<Usuario> cbUsuario;
    private ComboBox<Laboratorio> cbLaboratorio;
    private DatePicker dpData;
    private ComboBox<String> cbHorario;
    private TableView<Reserva> tvReservas;

    public TelaReservaController(ComboBox<Usuario> cbUsuario, ComboBox<Laboratorio> cbLaboratorio,
                                 DatePicker dpData, ComboBox<String> cbHorario, TableView<Reserva> tvReservas) {
        this.cbUsuario = cbUsuario;
        this.cbLaboratorio = cbLaboratorio;
        this.dpData = dpData;
        this.cbHorario = cbHorario;
        this.tvReservas = tvReservas;
    }

    public void inicializarDados() {
        atualizarComponentesSelecao();
        atualizarTabela(false);
    }

    // Define os usuários, laboratórios e os horários fracionados do IFPB
    public void atualizarComponentesSelecao() {
        cbUsuario.setItems(FXCollections.observableArrayList(Repositorio.usuarios));
        cbLaboratorio.setItems(FXCollections.observableArrayList(Repositorio.laboratorios));

        // Horários fracionados por blocos de aula no IFPB
        cbHorario.setItems(FXCollections.observableArrayList(
                "Manhã: 1ª/2ª Aula (07:30 - 09:30)",
                "Manhã: 3ª/4ª Aula (09:30 - 11:30)",
                "Manhã: Turno Inteiro (07:30 - 11:30)",
                "Tarde: 1ª/2ª Aula (13:30 - 15:30)",
                "Tarde: 3ª/4ª Aula (15:30 - 17:30)",
                "Tarde: Turno Inteiro (13:30 - 17:30)"
        ));
    }

    // RF03 / RF05 - Solicitar Reserva
    public void acaoSolicitarReserva() {
        Usuario usuarioSel = cbUsuario.getValue();
        Laboratorio labSel = cbLaboratorio.getValue();
        LocalDate dataSel = dpData.getValue();
        String horarioSel = cbHorario.getValue();

        if (usuarioSel == null || labSel == null || dataSel == null || horarioSel == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha todos os campos da reserva.");
            return;
        }

        boolean disponivel = Repositorio.verificarDisponibilidade(labSel, dataSel, horarioSel);

        if (disponivel) {
            Repositorio.salvarReserva(usuarioSel, labSel, dataSel, horarioSel);
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Reserva efetuada! Status inicial: Pendente.");
            atualizarTabela(false);
            limparFormularioReserva();
        } else {
            exibirAlerta(Alert.AlertType.ERROR, "Conflito", "Esse laboratório já está reservado neste horário!");
        }
    }

    // RF01 - Cadastrar Usuário
    public void acaoCadastrarUsuario(String nome, String id, String tipo) {
        if (nome.isEmpty() || id.isEmpty() || tipo == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha todos os campos do usuário.");
            return;
        }
        Repositorio.cadastrarUsuario(nome, id, tipo);
        atualizarComponentesSelecao();
        exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário cadastrado com sucesso!");
    }

    // RF02 - Cadastrar Laboratório
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

    // RF08 - Atualizar Status da Reserva (com remoção inteligente se for Finalizada/Cancelada)
    public void acaoAlterarStatus(Reserva selecionada, String novoStatus) {
        if (selecionada == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione uma reserva na tabela antes de clicar em atualizar.");
            return;
        }

        if (novoStatus.equals("Finalizada") || novoStatus.equals("Cancelada")) {
            // Remove do repositório ativo para liberar espaço e horários
            Repositorio.reservas.remove(selecionada);
            exibirAlerta(Alert.AlertType.INFORMATION, "Reserva Concluída", "A reserva foi definida como " + novoStatus + " e removida do painel ativo.");
        } else {
            // Apenas altera o status (ex: de "Pendente" para "Em uso")
            selecionada.setStatus(novoStatus);
            exibirAlerta(Alert.AlertType.INFORMATION, "Status Atualizado", "Status alterado para: " + novoStatus);
        }

        atualizarTabela(false);
    }

    // RF07 e RF09 - Atualiza a tabela com controle do filtro diário
    public void atualizarTabela(boolean apenasHoje) {
        if (apenasHoje) {
            tvReservas.setItems(FXCollections.observableArrayList(Repositorio.obterReservasDoDia()));
        } else {
            tvReservas.setItems(FXCollections.observableArrayList(Repositorio.reservas));
        }
        tvReservas.refresh(); // Força o JavaFX a renderizar a alteração visual na mesma hora
    }

    public void refreshTableHoje(boolean apenasHoje) {
        atualizarTabela(apenasHoje);
    }

    private void limparFormularioReserva() {
        cbUsuario.setValue(null);
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