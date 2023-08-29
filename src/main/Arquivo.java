package main;

import java.time.LocalDateTime;

public class Arquivo {

    private String nome;
    private Diretorio pai;
    private String permissao;
    private LocalDateTime dataHoraAtual;
    private String conteudo;

    public Arquivo createfile(String parameters, Diretorio Atual, Diretorio raiz) {

        String texto = parameters;

        String[] partes = texto.split(" ",2);

        String primeiraParte;
        String segundaParte;

        // Verificando as partes separadas
        if (partes.length >= 2) {
            primeiraParte = partes[0];
            segundaParte = partes[1];
            
            System.out.println("Primeira parte: " + primeiraParte);
            System.out.println("Segunda parte: " + segundaParte);
        } else {
           primeiraParte = partes[0];
            segundaParte = "";
        }

        String nome;
        String caminho = Atual;

        int ultimaBarraIndex = texto.lastIndexOf("/");
        if (ultimaBarraIndex >= 0) {
            primeiraParte = texto.substring(0, ultimaBarraIndex);
            segundaParte = texto.substring(ultimaBarraIndex + 1);

            System.out.println("Primeira parte dividida: " + primeiraParte);
            System.out.println("Segunda parte dividida: " + segundaParte);
        } else {
            System.out.println("A string não contém / para dividir.");
        }
    


    

       
    

        return this;
    }

    // Getter para 'nome'
    public String getNome() {
        return nome;
    }

    // Setter para 'nome'
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getter para 'pai'
    public Diretorio getPai() {
        return pai;
    }

    // Setter para 'pai'
    public void setPai(Diretorio pai) {
        this.pai = pai;
    }

    // Getter para 'permissao'
    public String getPermissao() {
        return permissao;
    }

    // Setter para 'permissao'
    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }

    // Getter para 'dataHoraAtual'
    public LocalDateTime getDataHoraAtual() {
        return dataHoraAtual;
    }

    // Setter para 'dataHoraAtual'
    public void setDataHoraAtual(LocalDateTime dataHoraAtual) {
        this.dataHoraAtual = dataHoraAtual;
    }

    // Getter para 'conteudo'
    public String getConteudo() {
        return conteudo;
    }

    // Setter para 'pai'
    public void setPai(String conteudo) {
        this.conteudo = conteudo;
    }
}
