package main;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import operatingSystem.Kernel;

/**
 * Kernel desenvolvido pelo aluno. Outras classes criadas pelo aluno podem ser
 * utilizadas, como por exemplo: - Arvores; - Filas; - Pilhas; - etc...
 *
 * @author nome do aluno...
 */
public class MyKernel implements Kernel {

    private Diretorio raiz;
    private Diretorio atual;

    public MyKernel() {

        this.raiz = new Diretorio();
        raiz.mkdir("/", null, null);
        raiz.setPai(raiz);
        atual = raiz;

        System.out.println("-----raiz-----");
        System.out.println("nome: " + raiz.getNome());

    }

    public String ls(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: ls");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        result = atual.ls(parameters, atual, raiz);
        // fim da implementacao do aluno
        return result;
    }

    public String mkdir(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: mkdir");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno

        Diretorio novodiretorio = new Diretorio();
        novodiretorio.mkdir(parameters, atual, raiz);

        if (result.equals(null)) {
            return "Inválido";
        }

        // fim da implementacao do aluno
        return result;
    }

    public String cd(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        Diretorio temp;

        System.out.println("Chamada de Sistema: cd");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno

        temp = atual.caminhos(parameters, raiz, atual);
        if (temp != null) {
            atual = temp;
            System.out.println("Atual:" + atual.getNome());

        } else {

            result = "não existe";
        }

        Diretorio temptemp = atual;

        String currentDir = "";
        // indique o diretório atual. Por exemplo... /
        while (!temptemp.equals(raiz)) {

            if (temptemp.equals(atual)) {
                currentDir = temptemp.getNome() + currentDir;
            } else {
                currentDir = temptemp.getNome() + "/" + currentDir;
            }

            temptemp = temptemp.getPai();
        }

        currentDir = "/" + currentDir;

        // setando parte gráfica do diretorio atual
        operatingSystem.fileSystem.FileSytemSimulator.currentDir = currentDir;

        // fim da implementacao do aluno
        return result;
    }

    public String rmdir(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: rmdir");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        // fim da implementacao do aluno
        return result;
    }

    public String cp(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: cp");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        // fim da implementacao do aluno
        return result;
    }

    public String mv(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: mv");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        // fim da implementacao do aluno
        return result;
    }

    public String rm(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: rm");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        // fim da implementacao do aluno
        return result;
    }

    public String chmod(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: chmod");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        Diretorio novo = new Diretorio();
        result = novo.chmod(parameters,raiz,atual);

        // fim da implementacao do aluno
        return result;
    }

    public String createfile(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: createfile");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno

        Arquivo novo = new Arquivo();
        novo.createfile(parameters, atual, raiz); 

        // fim da implementacao do aluno
        return result;
    }

    public String cat(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: cat");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno

        result = atual.cat(parameters,raiz,atual);

        // fim da implementacao do aluno
        return result;
    }

    public String batch(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: batch");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        // fim da implementacao do aluno
        return result;
    }

    public String dump(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: dump");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        // fim da implementacao do aluno
        return result;
    }

    public String info() {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: info");
        System.out.println("\tParametros: sem parametros");

        // nome do aluno
        String name = "Jônata Martins de Sousa";
        // numero de matricula
        String registration = "201911020008";
        // versao do sistema de arquivos
        String version = "0.9";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }


    // Feitos-------------------------
    //Info
    //MKDIR
    //CD
    //CREATEFILE
    //LS
    //CAT


}
