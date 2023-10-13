package main;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import operatingSystem.Kernel;
import hardware.HardDisk;
import static binary.Binario.binaryStringToInt;
import static binary.Binario.intToBinaryString;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kernel desenvolvido pelo aluno. Outras classes criadas pelo aluno podem ser
 * utilizadas, como por exemplo: - Arvores; - Filas; - Pilhas; - etc...
 *
 * @author nome do aluno...
 */
public class MyKernel2 implements Kernel {

    int raiz = 0;
    int dirAtual = raiz;
    HardDisk HD = new HardDisk(128);

    public MyKernel2() {
        int byteTamanho = 8;
        int blocoTamanho = 512 * byteTamanho;
        String dirRaiz = criaDir("/", raiz);
        salvaNoHD(HD, dirRaiz, 0);
        procuraFilho(HD, dirAtual);
    }

    public static boolean[] stringToBinaryArray(String input) {
        boolean[] binaryArray = new boolean[input.length() * 8]; // Cada caractere é convertido em 8 bits

        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);

            // Convertendo cada caractere em binário
            String binaryString = Integer.toBinaryString(character);

            // Preenchendo com zeros à esquerda, se necessário
            while (binaryString.length() < 8) {
                binaryString = "0" + binaryString;
            }

            // Armazenando os bits no array de booleanos
            for (int j = 0; j < 8; j++) {
                binaryArray[i * 8 + j] = (binaryString.charAt(j) == '1');
            }
        }

        return binaryArray;
    }

    public static String binaryArrayToString(boolean[] binaryArray) {
        if (binaryArray.length % 8 != 0) {
            throw new IllegalArgumentException(
                    "O tamanho do vetor deve ser múltiplo de 8 para representação binária correta.");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < binaryArray.length; i += 8) {
            int charValue = 0;
            for (int j = 0; j < 8; j++) {
                charValue = (charValue << 1) | (binaryArray[i + j] ? 1 : 0);
            }
            sb.append((char) charValue);
        }

        return sb.toString();
    }

    public static void atualizaPaiDir(HardDisk hd, int dirPai, int dirFilho) {

        String resultado = lerHD(hd, dirPai, 512);

        String estado = resultado.substring(0, 1);
        String nome = resultado.substring(1, 87).replaceAll("\\s+", "");
        String pai = resultado.substring(87, 97).replaceAll("\\s+", "");
        String dirFilhos[] = new String[20];
        String dirArquivos = resultado.substring(297, 497).replaceAll("\\s+", "");
        String data = resultado.substring(497, 509).replaceAll("\\s+", "");
        String permissao = resultado.substring(509, 512).replaceAll("\\s+", "");

        for (int i = 0; i < 20; i++) {
            dirFilhos[i] = resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "");
        }

        boolean tem = false;

        for (int i = 0; i < 20; i++) {
            if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                dirFilhos[i] = String.format("%-" + 10 + "s", Integer.toString(dirFilho));
            
                tem = true;

                break;
            }
        }

        if (!tem) {
            System.out.println("Diretorio cheio");

        } else {
            StringBuilder filho = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                filho.append(dirFilhos[i]);
            }

            String atualizada = estado + nome + pai + filho + dirArquivos + data + permissao;

            System.out.println("String Atualizada: " + atualizada);
            salvaNoHD(hd, atualizada, dirPai);
        }

    }

    public String criaDir(String nome, int dirpai) {
        String estado = "d";
        nome = String.format("%-" + 86 + "s", nome);
        String pai = String.format("%-" + 10 + "s", Integer.toString(dirpai));
        String dirFilhos = String.format("%-" + 200 + "s", "");
        String dirArquivos = String.format("%-" + 200 + "s", "");
        LocalDateTime dataAtual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String data = dataAtual.format(formato);
        String permissao = "777";

        return estado + nome + pai + dirFilhos + dirArquivos + data + permissao;

    }

    public static void exibeHD(HardDisk hd, int dirAtual) {

        for (int i = 0; i < 512; i++) {
            System.out.println(i + ":" + lerHD(hd, dirAtual, 512).charAt(i));
        }

    }

    public static String lerHD(HardDisk hd, int posicao, int tamanho) {
        boolean[] bits = new boolean[tamanho * 8];

        for (int i = 0; i < bits.length; i++) {
            bits[i] = hd.getBitDaPosicao(posicao * 512 * 8 + i);

        }

        return binaryArrayToString(bits);
    }

    public static int procuraPosicaoVaziaHD(HardDisk hd) {
        for (int i = 0; i < 134217728; i++) {
            String info = lerHD(hd, i, 1);
            if (!info.equals("d") && !info.equals("a")) {
                System.out.println("ACHOU :" + i);
                return i;
            }
        }
        return -1;
    }

    public static int procuraPai(HardDisk hd, int diratual) {
        String resultado = lerHD(hd, diratual, 512);
        return Integer.parseInt(resultado.substring(87, 97).replaceAll("\\s+", ""));
    }

    public static int procuraFilho(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);

        String[] filhos = new String[20];

        for (int i = 0; i < 20; i++) {
            filhos[i] = resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "");
            System.out.println("Filho" + i + ":" + filhos[i]);

        }

        return 0;
    }

    public static void salvaNoHD(HardDisk hd, String texto, int posicao) {
        boolean[] bits = stringToBinaryArray(texto);

        for (int i = 0; i < bits.length; i++) {
            hd.setBitDaPosicao(bits[i], posicao * 512 * 8 + i);
        }
    }

    // COMEÇO DAS NÃO AUXILIARES
    // --------------------------------------------------------------------------------------------------

    public String ls(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: ls");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        System.out.println("ola pessoal!");

        // fim da implementacao do aluno
        return result;
    }

    public String mkdir(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: mkdir");
        System.out.println("\tParametros: " + parameters);

        String nome = parameters;
        int aux;

        if (nome.equals("/")) {
            System.out.println("Diretorio ja existe");
            return "Diretorio raiz ja existe";
        }

        // aqui vê se é pra começar na raiz ou no diretório atual
        // se for na raiz tira a primeira / pra separar legal se nao o primeiro item
        // fica vazio
        if (nome.startsWith("/")) {
            aux = raiz;
            nome = nome.substring(1);
        } else {
            aux = dirAtual;
        }

        // aqui separa os comandos
        String[] partes = nome.split("/");

        // aqui lê comando por comando
        for (String parte : partes) {

            if (parte.equals(".")) {
                // se dor . nao faz nada

            }

            else if (parte.equals("..")) {

                int pai = procuraPai(HD, aux);
                aux = pai;

            }

            else {
                boolean tem = false;

                int filho = procuraFilho(HD, aux);

                /*
                 * while(filho != 0){
                 * String resultado = lerHD(HD, filho, 512);
                 * String nomeFilho = resultado.substring(1, 87).replaceAll("\\s+", "");
                 * if(nomeFilho.equals(parte)){
                 * tem = true;
                 * aux = filho;
                 * break;
                 * }
                 * filho = Integer.parseInt(resultado.substring(97, 107).replaceAll("\\s+",
                 * ""));
                 * }
                 */

            }

        }

        criaDir(parameters, dirAtual);

        salvaNoHD(HD, criaDir(parameters, dirAtual), procuraPosicaoVaziaHD(HD));
        System.out.println("Proximo é o Atualiza");
        atualizaPaiDir(HD, procuraPai(HD, aux), aux);
        System.out.println("Proximo é o Exibe");
        exibeHD(HD, aux);

        // inicio da implementacao do aluno
        // fim da implementacao do aluno
        return result;
    }

    public String cd(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        String currentDir = "";
        System.out.println("Chamada de Sistema: cd");
        System.out.println("\tParametros: " + parameters);

        // inicio da implementacao do aluno
        // indique o diretório atual. Por exemplo... /
        currentDir = "/";

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
        String version = "0.1";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

}
