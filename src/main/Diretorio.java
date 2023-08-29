package main;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Diretorio {

    private String nome;
    private Diretorio pai;
    private String permissao;
    private List<Diretorio> filho;
    private List<Arquivo> arquivo;
    private LocalDateTime dataHoraAtual;
    // LocalDateTime.now() para pegar a data


    // Função do codigo MKDIR
    public Diretorio mkdir(String nome, Diretorio pai) {

        if (!nome.matches("^[a-zA-Z0-9].*") || nome.contains("/")) {
            if (pai == null) {
            } else {
                return null;
            }
        }

        this.nome = nome;
        this.pai = pai;
        this.permissao = "drwx";
        this.dataHoraAtual = LocalDateTime.now();
        this.filho = new ArrayList<>();

        if (pai != null) {
            pai.filho.add(this);
        }

        return this;
    }

    public Diretorio buscaDiretorioPeloNome(String nome) {
        if (nome.equals(".")) {
            return this;
        } else if (nome.equals("..")) {
            return this.pai;
        } else {
            for (Diretorio dir : filho) {
                if (dir.getNome().equals(nome)) {
                    return dir;
                }
            }
        }
        return null;
    }


    // Função do codigo LS

    public String ls(String parameters) {

        StringBuilder stringBuilder = new StringBuilder();

        // Aqui é o Ls sem parametros

        if (filho.isEmpty()) {
            return "Sem filhos";
        }

        else if (parameters.equals("")) {

            for (Diretorio Dfilho : filho) {
                stringBuilder.append(Dfilho.getNome() + " ");
            }
        }

        return stringBuilder.toString();

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
