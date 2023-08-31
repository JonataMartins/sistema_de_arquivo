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

    public Diretorio caminhos(String caminho, Diretorio raiz, Diretorio atual) {

        Diretorio aux;

        if (caminho.startsWith("/")) {
            aux = raiz;
            caminho = caminho.substring(1);

        } else {
            aux = atual;
        }

        String[] partes = caminho.split("/");
        for (String parte : partes) {
            System.out.println("Caminho " + parte);
        }

        for (String parte : partes) {
            boolean achou = false;
            if (parte.equals(".")) {
                achou = true;
            } else if (parte.equals("..")) {
                aux = aux.pai;
                achou = true;
            }

            else {
                for (Diretorio filhos : aux.filho) {
                    if (filhos.getNome().equals(parte)) {
                        aux = filhos;
                        achou = true;
                        break;
                    }
                }

            }

            if (!achou) {
                return null;
            }

        }

        return aux;
    }

    // Função do codigo MKDIR
    public Diretorio mkdir(String nome, Diretorio atual, Diretorio raiz) {

        // pra criar a raiz apenas
        if (raiz == null) {
            this.nome = "/";
            this.pai = null;
            this.permissao = "drwx";
            this.dataHoraAtual = LocalDateTime.now();
            this.filho = new ArrayList<>();

            return this;
        }

        Diretorio aux;

        // aqui vê se é pra começar na raiz ou no diretório atual
        // se for na raiz tira a primeira / pra separar legal se nao o primeiro item
        // fica vazio

        if (nome.startsWith("/")) {
            aux = raiz;
            nome = nome.substring(1);
        } else {
            aux = atual;
        }

        // aqui separa os comandos
        String[] partes = nome.split("/");

        // aqui lê comando por comando
        for (String parte : partes) {

            if (parte.equals(".")) {
                // se dor . nao faz nada

            } else if (parte.equals("..")) {
                // se for .. o aux vira o proprio pai como no cd ..

                if (aux.pai == null) {
                    return null;
                }
                aux = aux.pai;

            } else {
                boolean tem = false;

                for (Diretorio filho : aux.filho) {
                    // verifica se o filho existe
                    if (parte.equals(filho.nome)) {
                        // se existe ele entra nesse filho
                        aux = filho;
                        tem = true;
                        break;
                    }
                }

                if (tem == false) {
                    // se nao existe ele cria

                    Diretorio novo = new Diretorio();

                    if (!parte.matches("^[a-zA-Z0-9].*") || parte.contains("/")) {
                        if (pai == null) {
                        } else {
                            return null;
                        }

                    } else {

                        novo.nome = parte;
                        novo.pai = aux;
                        novo.permissao = "drwx";
                        novo.dataHoraAtual = LocalDateTime.now();
                        novo.filho = new ArrayList<>();

                        if (aux != null) {

                            aux.filho.add(novo);

                            // aqui ele ja entra na pasta que foi criada
                            aux = novo;
                        }

                    }

                }

            }

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
