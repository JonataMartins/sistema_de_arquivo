package main;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Diretorio {

    private String nome;
    private Diretorio pai;
    private String permissao;
    private List<Diretorio> filho;
    private LocalDateTime dataHoraAtual;
    //LocalDateTime.now() para pegar a data


    //Função do codigo MKDIR
    public Diretorio mkdir (String nome){
        Diretorio novo = new Diretorio(nome, this);
        this.filho.add(novo);
        return novo;
    }
    
    //Construtor
    public Diretorio(String nome, Diretorio pai) {
        this.nome = nome;
        this.pai = pai;
        this.permissao = "drwx";
        this.dataHoraAtual = LocalDateTime.now();
        this.filho = new ArrayList<Diretorio>();
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Diretorio getPai() {
        return pai;
    }

    public void setPai(Diretorio pai) {
        this.pai = pai;
    }

    public String getPermissao() {
        return permissao;
    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }

    public List<Diretorio> getFilho() {
        return filho;
    }

    public void setFilho(List<Diretorio> filho) {
        this.filho = filho;
    }

    public LocalDateTime getDataHoraAtual() {
        return dataHoraAtual;
    }

    public void setDataHoraAtual(LocalDateTime dataHoraAtual) {
        this.dataHoraAtual = dataHoraAtual;
    }

}
