package com.labreserve.labreserve;

import com.labreserve.labreserve.controller.TelaReservaController;
import com.labreserve.labreserve.model.Reserva;
import com.labreserve.labreserve.model.Usuario;
import com.labreserve.labreserve.model.Laboratorio;
import com.labreserve.labreserve.repository.Repositorio;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        mostrarTelaLogin(stage);
    }

    private void mostrarTelaLogin(Stage stage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Label lblTitulo = new Label("LabReserve - Login");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField tfEmail = new TextField();
        tfEmail.setPromptText("E-mail");
        tfEmail.setMaxWidth(250);

        PasswordField pfSenha = new PasswordField();
        pfSenha.setPromptText("Palavra-passe");
        pfSenha.setMaxWidth(250);

        Button btnLogin = new Button("Entrar");
        btnLogin.setPrefWidth(250);

        btnLogin.setOnAction(e -> {
            String email = tfEmail.getText();
            String senha = pfSenha.getText();
            Usuario usuarioLogado = Repositorio.realizarLogin(email, senha);

            if (usuarioLogado != null) {
                mostrarTelaPrincipal(stage, usuarioLogado);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email ou palavra-passe incorretos!");
                alert.showAndWait();
            }
        });

        root.getChildren().addAll(lblTitulo, tfEmail, pfSenha, btnLogin);
        Scene scene = new Scene(root, 400, 300);

        aplicarCSS(scene);
        btnLogin.getStyleClass().addAll("button", "btn-primary");

        stage.setScene(scene);
        stage.setTitle("LabReserve - Acesso");
        stage.show();
    }

    private void mostrarTelaPrincipal(Stage stage, Usuario usuarioLogado) {
        TabPane tabPane = new TabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // ------------------ TAB 1: RESERVAS E MONITORAMENTO ------------------
        Tab tabReservas = new Tab("Solicitar & Consultar Reservas");
        tabReservas.setClosable(false);

        // CABEÇALHO COM MENSAGEM DE BOAS-VINDAS E BOTÃO LOGOUT
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        Label lblUsuarioLogado = new Label("Bem-vindo, " + usuarioLogado.getNome() + " (" + usuarioLogado.getTipo() + ")");
        lblUsuarioLogado.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Empurra o botão de logout para a direita

        Button btnLogout = new Button("Sair da Conta");
        btnLogout.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLogout.setOnAction(e -> mostrarTelaLogin(stage));

        headerBox.getChildren().addAll(lblUsuarioLogado, spacer, btnLogout);

        // FORMULÁRIO DE RESERVA
        GridPane gridReserva = new GridPane();
        gridReserva.setHgap(10);
        gridReserva.setVgap(10);

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

        gridReserva.add(new Label("Laboratório:"), 0, 0); gridReserva.add(cbLaboratorio, 1, 0);
        gridReserva.add(new Label("Data:"), 0, 1); gridReserva.add(dpData, 1, 1);
        gridReserva.add(new Label("Horário:"), 0, 2); gridReserva.add(cbHorario, 1, 2);
        gridReserva.add(btnReservar, 1, 3);

        // TABELA DE RESERVAS
        TableView<Reserva> tvReservas = new TableView<>();
        tvReservas.setPrefHeight(220);
        VBox.setVgrow(tvReservas, Priority.ALWAYS);

        TableColumn<Reserva, String> colUser = new TableColumn<>("Usuário");
        colUser.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        TableColumn<Reserva, String> colLab = new TableColumn<>("Laboratório");
        colLab.setCellValueFactory(new PropertyValueFactory<>("laboratorio"));

        TableColumn<Reserva, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));

        TableColumn<Reserva, String> colHoraIn = new TableColumn<>("Início");
        colHoraIn.setCellValueFactory(new PropertyValueFactory<>("horarioInicio"));

        TableColumn<Reserva, String> colHoraFim = new TableColumn<>("Fim");
        colHoraFim.setCellValueFactory(new PropertyValueFactory<>("horarioFim"));

        TableColumn<Reserva, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colUser.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.20));
        colLab.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.20));
        colData.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.15));
        colHoraIn.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.15));
        colHoraFim.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.15));
        colStatus.prefWidthProperty().bind(tvReservas.widthProperty().multiply(0.14));

        tvReservas.getColumns().addAll(colUser, colLab, colData, colHoraIn, colHoraFim, colStatus);

        TelaReservaController controller = new TelaReservaController(
                usuarioLogado, cbLaboratorio, dpData, cbHorario, tvReservas
        );
        controller.inicializarDados();

        btnReservar.setOnAction(e -> controller.acaoSolicitarReserva());

        // BARRA DE CONTROLO DE RESERVAS
        HBox barControle = new HBox(10);
        barControle.setPadding(new Insets(5, 0, 5, 0));

        Button btnFiltroHoje = new Button("Ver Reservas de Hoje");
        Button btnFiltroTodas = new Button("Ver Todas");

        // NOVO BOTÃO DE CANCELAR (Cor Vermelha para destaque)
        Button btnCancelarReserva = new Button("Cancelar Reserva");
        btnCancelarReserva.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        ComboBox<String> cbStatusAlterar = new ComboBox<>();
        cbStatusAlterar.getItems().addAll("Pendente", "Em uso", "Finalizada", "Cancelada");
        cbStatusAlterar.setPromptText("Alterar Status...");

        Button btnMudarStatus = new Button("Atualizar Status");

        btnFiltroHoje.setOnAction(e -> controller.refreshTableHoje(true));
        btnFiltroTodas.setOnAction(e -> controller.atualizarTabela(false));

        btnCancelarReserva.setOnAction(e -> {
            Reserva sel = tvReservas.getSelectionModel().getSelectedItem();
            controller.acaoCancelarReserva(sel);
        });

        // CORREÇÃO DO ERRO DO ALERT AQUI:
        btnMudarStatus.setOnAction(e -> {
            Reserva sel = tvReservas.getSelectionModel().getSelectedItem();
            String novoStat = cbStatusAlterar.getValue();
            if (sel == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione uma reserva na tabela primeiro.");
                alert.setHeaderText("Atenção");
                alert.showAndWait();
            } else if (novoStat == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione um status para alterar.");
                alert.setHeaderText("Atenção");
                alert.showAndWait();
            } else {
                controller.acaoAlterarStatus(sel, novoStat);
            }
        });

        Separator separator = new Separator();
        HBox.setHgrow(separator, Priority.ALWAYS);
        separator.setVisible(false);

        barControle.getChildren().addAll(btnFiltroHoje, btnFiltroTodas, btnCancelarReserva, separator, cbStatusAlterar, btnMudarStatus);

        // Apenas o Administrador pode mudar o status das reservas
        cbStatusAlterar.setDisable(!usuarioLogado.getTipo().equals("Admin"));
        btnMudarStatus.setDisable(!usuarioLogado.getTipo().equals("Admin"));
        // Se for "Comum", escondemos as opções de status para não poluir o ecrã
        if (!usuarioLogado.getTipo().equals("Admin")) {
            cbStatusAlterar.setVisible(false);
            btnMudarStatus.setVisible(false);
        }

        Label lblPainelControle = new Label("Painel de Controle e Monitoramento:");
        lblPainelControle.setStyle("-fx-font-weight: bold;");

        // Layout Principal da Aba 1
        VBox layoutReservas = new VBox(10, headerBox, gridReserva, lblPainelControle, tvReservas, barControle);
        layoutReservas.setPadding(new Insets(15));
        VBox.setVgrow(layoutReservas, Priority.ALWAYS);
        tabReservas.setContent(layoutReservas);
        tabPane.getTabs().add(tabReservas);

        // ------------------ TAB 2: CADASTROS (SÓ ADMIN VÊ) ------------------
        if (usuarioLogado.getTipo().equals("Admin")) {
            Tab tabCadastros = new Tab("Cadastros Administrativos");
            tabCadastros.setClosable(false);

            VBox layoutCadastros = new VBox(20);
            layoutCadastros.setPadding(new Insets(15));
            VBox.setVgrow(layoutCadastros, Priority.ALWAYS);

            VBox boxUsuario = new VBox(10);
            Label lblCadUsuario = new Label("CADASTRAR NOVO USUÁRIO");
            lblCadUsuario.setStyle("-fx-font-weight: bold;");
            boxUsuario.getChildren().add(lblCadUsuario);

            GridPane gridUser = new GridPane();
            gridUser.setHgap(10); gridUser.setVgap(10);
            TextField tfNomeUser = new TextField();
            ComboBox<String> cbTipoUser = new ComboBox<>();
            cbTipoUser.getItems().addAll("Admin", "Comum");
            TextField tfEmailUser = new TextField();
            PasswordField pfSenhaUser = new PasswordField();
            Button btnCadUsuario = new Button("Cadastrar Usuário");

            tfNomeUser.setPrefWidth(220);
            cbTipoUser.setPrefWidth(220);
            tfEmailUser.setPrefWidth(220);
            pfSenhaUser.setPrefWidth(220);
            btnCadUsuario.setPrefWidth(220);

            gridUser.add(new Label("Nome:"), 0, 0); gridUser.add(tfNomeUser, 1, 0);
            gridUser.add(new Label("Tipo:"), 0, 1); gridUser.add(cbTipoUser, 1, 1);
            gridUser.add(new Label("E-mail:"), 0, 2); gridUser.add(tfEmailUser, 1, 2);
            gridUser.add(new Label("Senha:"), 0, 3); gridUser.add(pfSenhaUser, 1, 3);
            gridUser.add(btnCadUsuario, 1, 4);
            boxUsuario.getChildren().add(gridUser);

            btnCadUsuario.setOnAction(e -> {
                controller.acaoCadastrarUsuario(tfNomeUser.getText(), cbTipoUser.getValue(), tfEmailUser.getText(), pfSenhaUser.getText());
                tfNomeUser.clear(); cbTipoUser.setValue(null); tfEmailUser.clear(); pfSenhaUser.clear();
            });

            VBox boxLab = new VBox(10);
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

            btnCadUsuario.getStyleClass().addAll("button", "btn-primary");
            btnCadLab.getStyleClass().addAll("button", "btn-primary");
            boxUsuario.getStyleClass().add("cadastro-box");
            boxLab.getStyleClass().add("cadastro-box");
        }

        VBox rootContainer = new VBox(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(rootContainer, 800, 680);
        aplicarCSS(scene);

        btnReservar.getStyleClass().addAll("button", "btn-success");
        btnMudarStatus.getStyleClass().addAll("button", "btn-primary");
        btnFiltroHoje.getStyleClass().addAll("button", "btn-secondary");
        btnFiltroTodas.getStyleClass().addAll("button", "btn-secondary");

        stage.setScene(scene);
        stage.setTitle("LabReserve - Logado como: " + usuarioLogado.getNome());
        stage.show();
    }

    private void aplicarCSS(Scene scene) {
        try {
            String cssPath = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception ex) {
            try {
                String cssPath = getClass().getResource("style.css").toExternalForm();
                scene.getStylesheets().add(cssPath);
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        launch();
    }
}