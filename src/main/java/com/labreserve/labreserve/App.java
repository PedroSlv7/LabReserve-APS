package com.labreserve.labreserve;

import com.labreserve.labreserve.controller.TelaReservaController;
import com.labreserve.labreserve.model.Reserva;
import com.labreserve.labreserve.model.Usuario;
import com.labreserve.labreserve.model.Laboratorio;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("LabReserve - Controle de Laboratórios");

        TabPane tabPane = new TabPane();

        // Garante que o TabPane principal cresça para ocupar a janela
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // ------------------ TAB 1: RESERVAS E MONITORAMENTO ------------------
        Tab tabReservas = new Tab("Solicitar & Consultar Reservas");
        tabReservas.setClosable(false);

        GridPane gridReserva = new GridPane();
        gridReserva.setPadding(new Insets(15));
        gridReserva.setHgap(10);
        gridReserva.setVgap(10);

        // --- FORMULÁRIO SUPERIOR: Mantido com tamanhos fixos para não esticar ---
        ComboBox<Usuario> cbUsuario = new ComboBox<>();
        cbUsuario.setPromptText("Selecione o Usuário");
        cbUsuario.setPrefWidth(220);

        ComboBox<Laboratorio> cbLaboratorio = new ComboBox<>();
        cbLaboratorio.setPromptText("Selecione o Laboratório");
        cbLaboratorio.setPrefWidth(220);

        DatePicker dpData = new DatePicker();
        dpData.setPrefWidth(220);

        ComboBox<String> cbHorario = new ComboBox<>();
        cbHorario.setPromptText("Selecione o Horário");
        cbHorario.setPrefWidth(220);

        Button btnReservar = new Button("Solicitar Reserva");
        btnReservar.setPrefWidth(220);

        gridReserva.add(new Label("Usuário:"), 0, 0); gridReserva.add(cbUsuario, 1, 0);
        gridReserva.add(new Label("Laboratório:"), 0, 1); gridReserva.add(cbLaboratorio, 1, 1);
        gridReserva.add(new Label("Data:"), 0, 2); gridReserva.add(dpData, 1, 2);
        gridReserva.add(new Label("Horário:"), 0, 3); gridReserva.add(cbHorario, 1, 3);
        gridReserva.add(btnReservar, 1, 4);

        // --- PAINEL DE CONTROLE (TABELA): Responsiva para preencher o espaço restante ---
        TableView<Reserva> tvReservas = new TableView<>();
        tvReservas.setPrefHeight(220);

        // Permite que a tabela estique verticalmente se a tela crescer
        VBox.setVgrow(tvReservas, Priority.ALWAYS);

        TableColumn<Reserva, String> colUser = new TableColumn<>("Usuário");
        colUser.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        TableColumn<Reserva, String> colLab = new TableColumn<>("Laboratório");
        colLab.setCellValueFactory(new PropertyValueFactory<>("laboratorio"));

        TableColumn<Reserva, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));

        TableColumn<Reserva, String> colHora = new TableColumn<>("Horário");
        colHora.setCellValueFactory(new PropertyValueFactory<>("horario"));

        TableColumn<Reserva, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Responsividade das colunas da tabela (distribuição percentual proporcional)
        colUser.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.20));
        colLab.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.25));
        colData.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.15));
        colHora.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.25));
        colStatus.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.14));

        tvReservas.getColumns().addAll(colUser, colLab, colData, colHora, colStatus);

        // Instanciando o Controller
        TelaReservaController controller = new TelaReservaController(
                cbUsuario, cbLaboratorio, dpData, cbHorario, tvReservas
        );
        controller.inicializarDados();

        btnReservar.setOnAction(e -> controller.acaoSolicitarReserva());

        // Controles de Status e Filtro Diário
        HBox barControle = new HBox(10);
        barControle.setPadding(new Insets(5, 0, 5, 0));

        Button btnFiltroHoje = new Button("Ver Reservas de Hoje");
        Button btnFiltroTodas = new Button("Ver Todas as Reservas");

        ComboBox<String> cbStatusAlterar = new ComboBox<>();
        cbStatusAlterar.getItems().addAll("Pendente", "Em uso", "Finalizada", "Cancelada");
        cbStatusAlterar.setPromptText("Alterar Status...");

        Button btnMudarStatus = new Button("Atualizar Status");

        btnFiltroHoje.setOnAction(e -> controller.refreshTableHoje(true));
        btnFiltroTodas.setOnAction(e -> controller.atualizarTabela(false));

        btnMudarStatus.setOnAction(e -> {
            Reserva sel = tvReservas.getSelectionModel().getSelectedItem();
            String novoStat = cbStatusAlterar.getValue();
            if (sel == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione uma reserva na tabela antes de atualizar.");
                alert.showAndWait();
            } else if (novoStat == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione um status no seletor ao lado.");
                alert.showAndWait();
            } else {
                controller.acaoAlterarStatus(sel, novoStat);
            }
        });

        // Divisor invisível inteligente para empurrar o controle de status para a direita na tela cheia
        Separator separator = new Separator();
        HBox.setHgrow(separator, Priority.ALWAYS);
        separator.setVisible(false);

        barControle.getChildren().addAll(btnFiltroHoje, btnFiltroTodas, separator, cbStatusAlterar, btnMudarStatus);

        // TÍTULO DO PAINEL DE CONTROLE EM NEGRITO
        Label lblPainelControle = new Label("Painel de Controle e Monitoramento:");
        lblPainelControle.setStyle("-fx-font-weight: bold;");

        VBox layoutReservas = new VBox(10, gridReserva, lblPainelControle, tvReservas, barControle);
        layoutReservas.setPadding(new Insets(10));
        VBox.setVgrow(layoutReservas, Priority.ALWAYS);
        tabReservas.setContent(layoutReservas);
        tabPane.getTabs().add(tabReservas);


        // ------------------ TAB 2: CADASTROS (RF01 e RF02) ------------------
        Tab tabCadastros = new Tab("Cadastros Administrativos");
        tabCadastros.setClosable(false);

        VBox layoutCadastros = new VBox(20);
        layoutCadastros.setPadding(new Insets(15));
        VBox.setVgrow(layoutCadastros, Priority.ALWAYS);

        // Cadastro de Usuário (Mantido com formulário compacto padrão)
        VBox boxUsuario = new VBox(10);

        // TÍTULO DE CADASTRO DE USUÁRIO EM NEGRITO
        Label lblCadUsuario = new Label("CADASTRAR NOVO USUÁRIO");
        lblCadUsuario.setStyle("-fx-font-weight: bold;");
        boxUsuario.getChildren().add(lblCadUsuario);

        GridPane gridUser = new GridPane();
        gridUser.setHgap(10); gridUser.setVgap(10);
        TextField tfNomeUser = new TextField();
        TextField tfIdUser = new TextField();
        ComboBox<String> cbTipoUser = new ComboBox<>();
        cbTipoUser.getItems().addAll("Professor", "Aluno");
        Button btnCadUsuario = new Button("Cadastrar Usuário");

        tfNomeUser.setPrefWidth(220);
        tfIdUser.setPrefWidth(220);
        cbTipoUser.setPrefWidth(220);
        btnCadUsuario.setPrefWidth(220);

        gridUser.add(new Label("Nome:"), 0, 0); gridUser.add(tfNomeUser, 1, 0);
        gridUser.add(new Label("Identificação:"), 0, 1); gridUser.add(tfIdUser, 1, 1);
        gridUser.add(new Label("Tipo:"), 0, 2); gridUser.add(cbTipoUser, 1, 2);
        gridUser.add(btnCadUsuario, 1, 3);
        boxUsuario.getChildren().add(gridUser);

        btnCadUsuario.setOnAction(e -> {
            controller.acaoCadastrarUsuario(tfNomeUser.getText(), tfIdUser.getText(), cbTipoUser.getValue());
            tfNomeUser.clear(); tfIdUser.clear(); cbTipoUser.setValue(null);
        });

        // Cadastro de Laboratório (Mantido com formulário compacto padrão)
        VBox boxLab = new VBox(10);

        // TÍTULO DE CADASTRO DE LABORATÓRIO EM NEGRITO
        Label lblCadLab = new Label("CADASTRAR NOVO LABORATÓRIO");
        lblCadLab.setStyle("-fx-font-weight: bold;");
        boxLab.getChildren().add(lblCadLab);

        GridPane gridLab = new GridPane();
        gridLab.setHgap(10); gridLab.setVgap(10);
        TextField tfNomeLab = new TextField();
        TextField tfCapLab = new TextField();
        TextField tfRecursosLab = new TextField();
        Button btnCadLab = new Button("Cadastrar Laboratório");

        tfNomeLab.setPrefWidth(220);
        tfCapLab.setPrefWidth(220);
        tfRecursosLab.setPrefWidth(220);
        btnCadLab.setPrefWidth(220);

        gridLab.add(new Label("Nome:"), 0, 0); gridLab.add(tfNomeLab, 1, 0);
        gridLab.add(new Label("Capacidade:"), 0, 1); gridLab.add(tfCapLab, 1, 1);
        gridLab.add(new Label("Recursos:"), 0, 2); gridLab.add(tfRecursosLab, 1, 2);
        gridLab.add(btnCadLab, 1, 3);
        boxLab.getChildren().add(gridLab);

        btnCadLab.setOnAction(e -> {
            controller.acaoCadastrarLaboratorio(tfNomeLab.getText(), tfCapLab.getText(), tfRecursosLab.getText());
            tfNomeLab.clear(); tfCapLab.clear(); tfRecursosLab.clear();
        });

        layoutCadastros.getChildren().addAll(boxUsuario, boxLab);
        tabCadastros.setContent(layoutCadastros);
        tabPane.getTabs().add(tabCadastros);

        VBox rootContainer = new VBox(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(rootContainer, 800, 680);

        // --- ATRIBUIÇÃO DOS ESTILOS CSS ---
        try {
            String cssPath = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception ex) {
            try {
                String cssPath = getClass().getResource("style.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
            } catch (Exception ignored) {}
        }

        // --- VINCULANDO AS CLASSES DO CSS ---
        btnReservar.getStyleClass().clear();
        btnReservar.getStyleClass().addAll("button", "btn-success");

        btnMudarStatus.getStyleClass().clear();
        btnMudarStatus.getStyleClass().addAll("button", "btn-primary");

        btnFiltroHoje.getStyleClass().clear();
        btnFiltroHoje.getStyleClass().addAll("button", "btn-secondary");

        btnFiltroTodas.getStyleClass().clear();
        btnFiltroTodas.getStyleClass().addAll("button", "btn-secondary");

        btnCadUsuario.getStyleClass().clear();
        btnCadUsuario.getStyleClass().addAll("button", "btn-primary");

        btnCadLab.getStyleClass().clear();
        btnCadLab.getStyleClass().addAll("button", "btn-primary");

        boxUsuario.getStyleClass().clear();
        boxUsuario.getStyleClass().add("cadastro-box");

        boxLab.getStyleClass().clear();
        boxLab.getStyleClass().add("cadastro-box");

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}