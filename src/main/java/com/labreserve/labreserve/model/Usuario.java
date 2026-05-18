package com.labreserve.labreserve.model;

public class Usuario {
    private int idUsuario; // Nome igual ao diagrama
    private String nome;
    private String tipo;   // "Admin" ou "Comum"
    private String email;  // Novo atributo do diagrama
    private String senha;  // Novo atributo do diagrama

    public Usuario(int idUsuario, String nome, String tipo, String email, String senha) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.tipo = tipo;
        this.email = email;
        this.senha = senha;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    // Método exigido no diagrama de classes
    public boolean fazerLogin(String emailTentativa, String senhaTentativa) {
        return this.email.equals(emailTentativa) && this.senha.equals(senhaTentativa);
    }

    @Override
    public String toString() {
        return nome + " (" + tipo + ")"; // Facilita a exibição na tela
    }
}