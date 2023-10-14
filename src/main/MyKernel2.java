package main;

import operatingSystem.Kernel;
import hardware.HardDisk;
import static binary.Binario.binaryStringToInt;
import static binary.Binario.intToBinaryString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Kernel desenvolvido pelo aluno. Outras classes criadas pelo aluno podem ser
 * utilizadas, como por exemplo: - Arvores; - Filas; - Pilhas; - etc...
 *
 * @author Jônata Martins de Sousa
 */
public class MyKernel2 implements Kernel {

    int raiz = 0;
    int dirAtual = raiz;
    HardDisk HD = new HardDisk(128);

    public MyKernel2() {
        // int byteTamanho = 8;
        // int blocoTamanho = 512 * byteTamanho;
        String dirRaiz = criaDir("/", raiz);
        salvaNoHD(HD, dirRaiz, 0);

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
        String nome = resultado.substring(1, 87);
        String pai = resultado.substring(87, 97);
        String dirFilhos[] = new String[20];
        String dirArquivos[] = new String[20];
        String data = resultado.substring(497, 509);
        String permissao = resultado.substring(509, 512);

        for (int i = 0; i < 20; i++) {
            dirFilhos[i] = resultado.substring(97 + (i * 10), 107 + (i * 10));
        }

        for (int i = 0; i < 20; i++) {
            dirArquivos[i] = resultado.substring(297 + (i * 10), 307 + (i * 10));
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

        } else {

            StringBuilder filho = new StringBuilder();
            filho.append(estado);
            filho.append(nome);
            filho.append(pai);
            for (int i = 0; i < 20; i++) {
                filho.append(dirFilhos[i]);
            }

            for (int i = 0; i < 20; i++) {
                filho.append(dirArquivos[i]);
            }

            filho.append(data);
            filho.append(permissao);

            String converte = filho.toString();
            salvaNoHD(hd, converte, dirPai);

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

    public static int filhocheio(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);

        for (int i = 0; i < 20; i++) {
            if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                return 0;
            }
        }

        // se tiver cheio;
        return -1;
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
        return -10;
    }

    public static int procuraDiretorio(HardDisk hd, String caminho, int dirAtual) {

        int aux;

        if (caminho.equals("/")) {

            return 0;
        }

        // se o primeiro for / quer dizer que começa na raiz se nao começa no atual
        // diretorio
        if (caminho.startsWith("/")) {
            aux = 0;
            caminho = caminho.substring(1);

        } else {
            aux = dirAtual;
        }

        String[] partes = caminho.split("/");

        for (String p : partes) {

            if (p.equals(".")) {

            } else if (p.equals("..")) {
                int pai = procuraPai(hd, aux);
                aux = pai;

            } else {
                int PF = procuraFilho(hd, aux, p);
                if (PF == -1) {
                    return -1;
                } else {
                    aux = PF;

                }
            }

        }

        return aux;

    }

    public static int procuraPai(HardDisk hd, int diratual) {
        String resultado = lerHD(hd, diratual, 512);
        return Integer.parseInt(resultado.substring(87, 97).replaceAll("\\s+", ""));
    }

    public static int procuraFilho(HardDisk hd, int dirAtual, String nome) {
        String resultado = lerHD(hd, dirAtual, 512);
        String filho;

        for (int i = 0; i < 20; i++) {

            if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {

            } else {
                filho = retornaNome(hd,
                        binaryStringToInt(resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                if (filho.equals(nome)) {

                    return binaryStringToInt(resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", ""));
                }

            }

        }

        // se tiver cheio;
        return -1;
    }

    public static void salvaNoHD(HardDisk hd, String texto, int posicao) {
        boolean[] bits = stringToBinaryArray(texto);

        for (int i = 0; i < bits.length; i++) {
            hd.setBitDaPosicao(bits[i], posicao * 512 * 8 + i);
        }
    }

    // Retorno de Nomes ----------------------------------------------
    public static String retornaNome(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);
        return resultado.substring(1, 87).replaceAll("\\s+", "");
    }

    public static String retornaNomePai(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);
        return resultado.substring(87, 97).replaceAll("\\s+", "");
    }

    public static String retornaData(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);
        return resultado.substring(497, 509).replaceAll("\\s+", "");
    }

    public static String retornaPermissao(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);
        return resultado.substring(509, 512).replaceAll("\\s+", "");
    }

    public static String retornaEstado(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);
        return resultado.substring(0, 1).replaceAll("\\s+", "");
    }

    public static String convertePermissao(String permissao) {
        StringBuilder p = new StringBuilder();
        char[] partes = permissao.toCharArray();

        for (char parte : partes) {
            String perm;
            switch (parte) {
                case '0':
                    perm = "---";

                    break;
                case '1':
                    perm = "--x";

                    break;
                case '2':
                    perm = "-w-";

                    break;
                case '3':
                    perm = "-wx";

                    break;
                case '4':
                    perm = "r--";

                    break;
                case '5':
                    perm = "r-x";

                    break;
                case '6':
                    perm = "rw-";

                    break;
                case '7':
                    perm = "rwx";

                    break;
                default:
                    perm = "";

                    break;
            }
            p.append(perm);
        }

        String g = p.toString();
        return g;
    }

    public static String converteData(String numeroData) {

        String ano = numeroData.substring(0, 4);
        String mes = numeroData.substring(4, 6);
        String dia = numeroData.substring(6, 8);
        String hora = numeroData.substring(8, 10);
        String minuto = numeroData.substring(10, 12);

        return dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
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

        if (parameters.equals("")) {
            String resultado = lerHD(HD, dirAtual, 512);

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < 20; i++) {
                if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                } else {
                    sb.append(retornaNome(HD,
                            binaryStringToInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", ""))));
                    sb.append("  ");

                }

            }

            for (int i = 0; i < 20; i++) {
                if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                } else {
                    sb.append(retornaNome(HD,
                            binaryStringToInt(
                                    resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", ""))));
                    sb.append("  ");

                }

            }

            result = sb.toString();

            if (result.equals("")) {
                result = "Diretorio vazio";
            }
        }

        else {
            String[] partes = parameters.split(" ");

            String l;
            String caminho;

            if (partes.length >= 2) {
                l = partes[0];
                caminho = partes[1];

                int aux = procuraDiretorio(HD, caminho, dirAtual);

                if (aux == -1) {
                    return "Diretorio nao existe";
                } else {
                    String resultado = lerHD(HD, aux, 512);

                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {
                            sb.append(retornaNome(HD,
                                    binaryStringToInt(
                                            resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
                            sb.append("  ");

                            String data = retornaData(HD, binaryStringToInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(converteData(data));

                            sb.append("  ");

                            String perm = retornaPermissao(HD, binaryStringToInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(convertePermissao(perm));

                            sb.append("\n");

                        }
                    }

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {
                            sb.append(retornaNome(HD,
                                    binaryStringToInt(
                                            resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
                            sb.append("  ");

                            String data = retornaData(HD, binaryStringToInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(converteData(data));

                            sb.append("  ");

                            String perm = retornaPermissao(HD, binaryStringToInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(convertePermissao(perm));
                            sb.append("\n");
                        }
                    }

                    result = sb.toString();

                    if (result.equals("")) {
                        result = "Diretorio vazio";
                    }
                }

            } else {
                if (parameters.equals("-l")) {

                    int aux = dirAtual;

                    String resultado = lerHD(HD, aux, 512);

                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {
                            sb.append(retornaNome(HD,
                                    binaryStringToInt(
                                            resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
                            sb.append("  ");

                            String data = retornaData(HD, binaryStringToInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(converteData(data));

                            sb.append("  ");

                            String perm = retornaPermissao(HD, binaryStringToInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(convertePermissao(perm));
                            sb.append("\n");
                        }
                    }

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {
                            sb.append(retornaNome(HD,
                                    binaryStringToInt(
                                            resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
                            sb.append("  ");

                            String data = retornaData(HD, binaryStringToInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(converteData(data));

                            sb.append("  ");

                            String perm = retornaPermissao(HD, binaryStringToInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(convertePermissao(perm));
                            sb.append("\n");
                        }
                    }

                    result = sb.toString();

                    if (result.equals("")) {
                        result = "Diretorio vazio";
                    }

                } else {
                    caminho = parameters;

                    StringBuilder sb = new StringBuilder();

                    int aux = procuraDiretorio(HD, caminho, dirAtual);

                    if (aux == -1) {
                        return "Diretorio nao existe";
                    }

                    String resultado = lerHD(HD, aux, 512);

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {
                            sb.append(retornaNome(HD,
                                    binaryStringToInt(
                                            resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
                            sb.append("  ");

                        }

                    }

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {
                            sb.append(retornaNome(HD,
                                    binaryStringToInt(
                                            resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
                            sb.append("  ");

                        }

                    }

                    result = sb.toString();

                    if (result.equals("")) {
                        result = "Diretorio vazio";
                    }

                }

            }
        }

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

                int PF = procuraFilho(HD, aux, parte);
                int FC = filhocheio(HD, aux);

                if (FC == -1) {
                    return "Qunatidade de diretorio cheio";
                } else {
                    if (PF == -1) {
                        String criado = criaDir(parte, aux);
                        int posicao = procuraPosicaoVaziaHD(HD);
                        if (posicao == -10) {
                            return "HD cheio";
                        }
                        if (!parte.matches("^[a-zA-Z0-9].*")) {
                            return "Nome invalido";
                        }

                        salvaNoHD(HD, criado, posicao);
                        atualizaPaiDir(HD, aux, posicao);
                        result = "Diretorio criado";
                        aux = posicao;

                    } else {
                        result = "Diretorio ja existe";
                        aux = PF;
                    }
                }

            }

        }

        // exibeHD(HD, aux);

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
        int aux = procuraDiretorio(HD, parameters, dirAtual);

        if (aux == -1) {
            return "Diretorio nao existe";
        } else {
            dirAtual = aux;
            currentDir = retornaNome(HD, dirAtual);
        }

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
        String version = "0.41";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

    // Funções 100% implementadas
    // info
    // cd
    // mkdir
    // ls

    // Funções em andamento
    //

}
