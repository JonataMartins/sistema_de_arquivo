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

        String[] partes = texto.split(" ", 2);

        String primeiraParte;
        String segundaParte;

        String nomeArquivo;
        String conteudoArquivo;

        // Verificando as partes separadas
        if (partes.length >= 2) {
            nomeArquivo = partes[0];
            conteudoArquivo = partes[1];

            System.out.println("Primeira parte: " + nomeArquivo);
            System.out.println("Segunda parte: " + conteudoArquivo);
        } else {

            nomeArquivo = partes[0];
            conteudoArquivo = "";
        }

        int ultimaBarraIndex = texto.lastIndexOf("/");
        if (ultimaBarraIndex >= 0) {

            primeiraParte = texto.substring(0, ultimaBarraIndex);
            segundaParte = texto.substring(ultimaBarraIndex + 1);

            int firstSpaceIndex = segundaParte.indexOf(' ');

            if (firstSpaceIndex != -1) {
                nomeArquivo = segundaParte.substring(0, firstSpaceIndex);
                conteudoArquivo = segundaParte.substring(firstSpaceIndex + 1);

                System.out.println("Nome arquivo: " + nomeArquivo);
                System.out.println("Conteudo: " + conteudoArquivo);
            } else {
                nomeArquivo = segundaParte;
                System.out.println("Nome arquivo: " + nomeArquivo);
                System.out.println("A string não contém espaço.");
            }
            
            String caminho = primeiraParte;

            System.out.println("Caminho " + caminho);

            Diretorio aux = new Diretorio();

            if (aux.caminhos(caminho, raiz, Atual) == null) {
                System.out.println("Diretório não existente");
            } else {
                aux = aux.caminhos(caminho, raiz, Atual);
                System.out.println();

                for (Arquivo arquivo : aux.getArquivo()) {
                    if (arquivo.getNome().equals(nomeArquivo)) {
                        System.out.println("Arquivo Existente");
                        return null;
                    }
                }

                this.nome = nomeArquivo;
                this.pai = aux;
                this.permissao = "drwx";
                this.dataHoraAtual = LocalDateTime.now();
                this.conteudo = conteudoArquivo;

                aux.adicionaArquivo(this, pai);

            }

        } else {
            System.out.println("A string não contém / para dividir.");

            for (Arquivo arquivo : Atual.getArquivo()) {
                if (arquivo.getNome().equals(nomeArquivo)) {
                    System.out.println("Arquivo Existente");
                    return null;
                }
            }

            this.nome = nomeArquivo;
            this.pai = Atual;
            this.permissao = "drwx";
            this.dataHoraAtual = LocalDateTime.now();
            this.conteudo = conteudoArquivo;

            Atual.adicionaArquivo(this, Atual);

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
