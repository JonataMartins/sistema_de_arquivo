package main;

import operatingSystem.Kernel;
import hardware.HardDisk;
import static binary.Binario.binaryStringToInt;
import static binary.Binario.intToBinaryString;

import java.io.IOException;
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

    public static void limparTerminal() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                // Se estiver em um sistema Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Se estiver em um sistema Unix/Linux
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            // Lidar com exceções, se necessário
            e.printStackTrace();
        }
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

    public static void atualizaPermissaoGeral(HardDisk hd, int dirAtual, String permissao) {
        // 1 é pq existe algum filho
        if (existeFilho(hd, dirAtual) == 1) {
            int[] todosFilhos = new int[20];
            todosFilhos = retornaFilho(hd, dirAtual);

            for (int i = 0; i < 20; i++) {
                if (todosFilhos[i] != -1) {
                    atualizaPermissaoGeral(hd, todosFilhos[i], permissao);
                }
            }

            for (int i = 0; i < 20; i++) {
                int[] todosArquivos = retornaArquivo(hd, dirAtual); // Modificado para retornar arquivos de dirAtual

                if (todosArquivos[i] != -1) {
                    atualizaPermissaoArquivo(hd, todosArquivos[i], permissao);
                }
            }

            int[] tFilhos = retornaFilho(hd, dirAtual); // Modificado para retornar filhos de dirAtual
            for (int i = 0; i < 20; i++) {
                if (tFilhos[i] != -1) {
                    atualizaPermissaoDir(hd, tFilhos[i], permissao);
                }
            }
        } else {
            // O que fazer quando não há filhos?
        }
    }

    public static void atualizaPaiArquivo(HardDisk hd, int dirPai, int dirFilho) {

        String resultado = lerHD(hd, dirPai, 512);

        String estado = resultado.substring(0, 1);
        String nome = resultado.substring(1, 87);
        String pai = resultado.substring(87, 97);
        String data = resultado.substring(497, 509);
        String permissao = resultado.substring(509, 512);

        String[] dirArquivos = new String[20];
        String[] dirFilhos = new String[20];

        for (int i = 0; i < 20; i++) {
            dirArquivos[i] = resultado.substring(297 + (i * 10), 307 + (i * 10));
            dirFilhos[i] = resultado.substring(97 + (i * 10), 107 + (i * 10));
        }

        boolean tem = false;

        for (int i = 0; i < 20; i++) {
            if (dirArquivos[i].replaceAll("\\s+", "").equals("")) {
                dirArquivos[i] = String.format("%-" + 10 + "s", Integer.toString(dirFilho));
                tem = true;
                break;
            }
        }

        if (tem) {
            StringBuilder arquivo = new StringBuilder();
            arquivo.append(estado);
            arquivo.append(nome);
            arquivo.append(pai);
            for (int i = 0; i < 20; i++) {
                arquivo.append(dirFilhos[i]);
            }
            for (int i = 0; i < 20; i++) {
                arquivo.append(dirArquivos[i]);
            }

            arquivo.append(data);
            arquivo.append(permissao);

            String converte = arquivo.toString();
            salvaNoHD(hd, converte, dirPai);
        }
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

    public static void atualizaPermissaoArquivo(HardDisk hd, int arquivo, String perm) {
        String resultado = lerHD(hd, arquivo, 512);

        String estado = resultado.substring(0, 1);
        String nome = resultado.substring(1, 87);
        String pai = resultado.substring(87, 97);
        String data = resultado.substring(97, 109);
        String permissao = resultado.substring(109, 112);
        String conteudo = resultado.substring(112, 512);

        permissao = perm;

        String save = estado + nome + pai + data + permissao + conteudo;

        salvaNoHD(hd, save, arquivo);
    }

    public static void atualizaPermissaoDir(HardDisk hd, int dirAtual, String perm) {

        String resultado = lerHD(hd, dirAtual, 512);

        String estado = resultado.substring(0, 1);
        String nome = resultado.substring(1, 87);
        String pai = resultado.substring(87, 97);
        String dirFilhos[] = new String[20];
        String dirArquivos[] = new String[20];
        String data = resultado.substring(497, 509);
        String permissao = resultado.substring(509, 512);

        permissao = perm;

        for (int i = 0; i < 20; i++) {
            dirFilhos[i] = resultado.substring(97 + (i * 10), 107 + (i * 10));
        }

        for (int i = 0; i < 20; i++) {
            dirArquivos[i] = resultado.substring(297 + (i * 10), 307 + (i * 10));
        }

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
        salvaNoHD(hd, converte, dirAtual);

    }

    public static String criaArquivo(String nome, int dirpai, String conteudo) {
        String estado = "a";
        nome = String.format("%-" + 86 + "s", nome);
        String pai = String.format("%-" + 10 + "s", Integer.toString(dirpai));
        LocalDateTime dataAtual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String data = dataAtual.format(formato);
        String permissao = "777";
        String conteudoArquivo = conteudo + String.format("%-" + (400 - conteudo.length()) + "s", "");

        // createfile a Maria chata

        return estado + nome + pai + data + permissao + conteudoArquivo;
    }

    public static String criaDir(String nome, int dirpai) {
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

    public static String convertePermissao(String permissao, HardDisk hd, int dirAtual) {
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

    public static void exibeHD(HardDisk hd, int dirAtual) {

        for (int i = 0; i < 512; i++) {
            System.out.println(i + ":" + lerHD(hd, dirAtual, 512).charAt(i));
        }

    }

    public static int arquivocheio(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);

        for (int i = 0; i < 20; i++) {
            if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                return 0;
            }
        }

        // se tiver cheio;
        return -1;
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
                return i;
            }
        }
        return -10;
    }

    public static int buscaCaminho(HardDisk hd, String caminho, int dirAtual) {

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

    public static int procuraArquivo(HardDisk hd, int dirAtual, String nome) {
        String resultado = lerHD(hd, dirAtual, 512);
        String filho;

        for (int i = 0; i < 20; i++) {

            if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {

            } else {
                filho = retornaNome(hd,
                        Integer.parseInt(resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "")));

                if (filho.equals(nome)) {

                    return Integer.parseInt(
                            resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", ""));
                }

            }

        }

        return -1;
    }

    public static int procuraFilho(HardDisk hd, int dirAtual, String nome) {
        String resultado = lerHD(hd, dirAtual, 512);
        String filho;

        for (int i = 0; i < 20; i++) {

            if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {

            } else {
                filho = retornaNome(hd,
                        Integer.parseInt(resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                if (filho.equals(nome)) {

                    return Integer.parseInt(resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", ""));
                }

            }

        }

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

    public static String converteData(String numeroData) {

        String ano = numeroData.substring(0, 4);
        String mes = numeroData.substring(4, 6);
        String dia = numeroData.substring(6, 8);
        String hora = numeroData.substring(8, 10);
        String minuto = numeroData.substring(10, 12);

        return dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto;
    }

    public static String retornaDataArquivo(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);
        return resultado.substring(97, 109).replaceAll("\\s+", "");

    }

    public static String retornaPermissaoArquivo(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);
        return resultado.substring(109, 112).replaceAll("\\s+", "");

    }

    public static int[] retornaFilho(HardDisk hd, int dirAtual) {

        String resultado = lerHD(hd, dirAtual, 512);
        int[] filhos = new int[20];

        for (int i = 0; i < 20; i++) {
            if (!resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                filhos[i] = Integer.parseInt(
                        resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", ""));

            } else {
                filhos[i] = -1;
            }

        }

        return filhos;

    }

    public static int existeFilho(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);
        int[] filhos = new int[20];
        int cont = 0;
        for (int i = 0; i < 20; i++) {
            if (!resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                filhos[i] = Integer.parseInt(
                        resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", ""));
                cont++;
            }

        }

        // não
        if (cont == 0) {
            return 0;
        }
        // sim
        else {
            return 1;
        }

    }

    public static int existeArquivo(HardDisk hd, int dirAtual) {

        String resultado = lerHD(hd, dirAtual, 512);
        int[] arquivos = new int[20];
        int cont = 0;
        for (int i = 0; i < 20; i++) {
            if (!resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                arquivos[i] = Integer.parseInt(
                        resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", ""));
                cont++;
            }

        }

        // não
        if (cont == 0) {
            return 0;
        }
        // sim
        else {
            return 1;
        }

    }

    public static int[] retornaArquivo(HardDisk hd, int dirAtual) {
        String resultado = lerHD(hd, dirAtual, 512);
        int[] arquivos = new int[20];

        for (int i = 0; i < 20; i++) {
            if (!resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                arquivos[i] = Integer.parseInt(
                        resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", ""));

            } else {
                arquivos[i] = -1;
            }

        }

        return arquivos;

    }

    // COMEÇO DAS NÃO AUXILIARES
    // --------------------------------------------------------------------------------------------------

    public String ls(String parameters) {
        limparTerminal();
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
                    sb.append(retornaNome(HD, Integer.parseInt(resultado.substring(97 + (i * 10), 107 + (i * 10))
                            .replaceAll("\\s+", ""))));
                    sb.append("  ");

                }

            }

            for (int i = 0; i < 20; i++) {
                if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                } else {
                    sb.append(retornaNome(HD,
                            Integer.parseInt(
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

                int aux = buscaCaminho(HD, caminho, dirAtual);

                if (aux == -1) {
                    return "Diretorio nao existe";
                } else {
                    String resultado = lerHD(HD, aux, 512);

                    StringBuilder sb = new StringBuilder();

                    // Diretorio

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {

                            String perm = retornaPermissao(HD, Integer.parseInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append("d" + convertePermissao(perm, HD, aux));

                            sb.append("  ");

                            String data = retornaData(HD, Integer.parseInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(converteData(data));

                            sb.append("  ");

                            sb.append(retornaNome(HD,
                                    Integer.parseInt(
                                            resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+",
                                                    ""))));

                            sb.append("\n");

                        }
                    }

                    // Arquivo

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {

                            String perm = retornaPermissaoArquivo(HD, Integer.parseInt(
                                    resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append("a" + convertePermissao(perm, HD, aux));

                            sb.append("  ");

                            String data = retornaDataArquivo(HD, Integer.parseInt(
                                    resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(converteData(data));

                            sb.append("  ");

                            sb.append(retornaNome(HD,
                                    Integer.parseInt(
                                            resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
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
                            String perm = retornaPermissao(HD, Integer.parseInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append("d" + convertePermissao(perm, HD, aux));

                            sb.append("  ");

                            String data = retornaData(HD, Integer.parseInt(
                                    resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(converteData(data));

                            sb.append("  ");

                            sb.append(retornaNome(HD,
                                    Integer.parseInt(
                                            resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
                            sb.append("\n");
                        }
                    }

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {

                            String perm = retornaPermissaoArquivo(HD, Integer.parseInt(
                                    resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append("a" + convertePermissao(perm, HD, aux));

                            sb.append("  ");

                            String data = retornaDataArquivo(HD, Integer.parseInt(
                                    resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "")));

                            sb.append(converteData(data));

                            sb.append("  ");

                            sb.append(retornaNome(HD,
                                    Integer.parseInt(
                                            resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
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

                    int aux = buscaCaminho(HD, caminho, dirAtual);

                    if (aux == -1) {
                        return "Diretorio nao existe";
                    }

                    String resultado = lerHD(HD, aux, 512);

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {
                            sb.append(retornaNome(HD,
                                    Integer.parseInt(
                                            resultado.substring(97 + (i * 10), 107 + (i * 10)).replaceAll("\\s+",
                                                    ""))));
                            sb.append("  ");

                        }

                    }

                    for (int i = 0; i < 20; i++) {
                        if (resultado.substring(297 + (i * 10), 307 + (i * 10)).replaceAll("\\s+", "").equals("")) {
                        } else {
                            sb.append(retornaNome(HD,
                                    Integer.parseInt(
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

        // exibeHD(HD, dirAtual);
        // fim da implementacao do aluno
        return result;
    }

    public String mkdir(String parameters) {
        limparTerminal();
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
                int PA = procuraArquivo(HD, aux, parte);
                int FC = filhocheio(HD, aux);

                if (FC == -1) {
                    return "Qunatidade de diretorio cheio";
                } else {
                    if (PF == -1 && PA == -1) {
                        String criado = criaDir(parte, aux);
                        int posicao = procuraPosicaoVaziaHD(HD);
                        if (posicao == -10) {
                            return "HD cheio";
                        }
                        if (!parte.matches("^[a-zA-Z0-9]*")) {
                            return "Nome invalido";
                        }

                        salvaNoHD(HD, criado, posicao);
                        atualizaPaiDir(HD, aux, posicao);
                        result = "Diretorio criado";
                        aux = posicao;

                    } else {
                        result = "Algum diretorio ou arquivo ja possui esse nome";
                        aux = PF;
                    }
                }

            }

        }

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
        int aux = buscaCaminho(HD, parameters, dirAtual);

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

        String[] partes = parameters.split(" ");
        String permissao;
        String caminho;
        int aux = dirAtual;

        if (partes.length <= 1) {
            return "Erro Parametros insuficientes";

        }

        else if (partes.length == 2) {
            permissao = partes[0];
            caminho = partes[1];

            if (permissao.length() != 3) {
                return "Permissao invalida, digite 3 numeros de 0 a 7";
            }
            if (!permissao.matches("[0-7]+")) {
                return "Permissao invalida, digite apenas numeros de 0 a 7";
            }

            if (permissao.equals("-r") || permissao.equals("-R") || caminho.equals("-r") || caminho.equals("-R")) {
                return "Permissao invalida";
            }

            int ultimaBarraIndex = caminho.lastIndexOf("/");
            String nomeArquivo;
            String caminhoNovo;

            if (ultimaBarraIndex >= 0) {
                if (ultimaBarraIndex == 0) {
                    caminhoNovo = "/";
                    nomeArquivo = caminho.substring(ultimaBarraIndex + 1);

                    aux = buscaCaminho(HD, caminhoNovo, dirAtual);
                } else {
                    caminhoNovo = caminho.substring(0, ultimaBarraIndex);
                    nomeArquivo = caminho.substring(ultimaBarraIndex + 1);

                    aux = buscaCaminho(HD, caminhoNovo, dirAtual);

                }

            } else {

                caminhoNovo = "";
                nomeArquivo = caminho;

                aux = dirAtual;

            }
            if (aux == -1) {
                return "Diretorio nao existe";

            } else {
                int eArquivo = procuraArquivo(HD, aux, nomeArquivo);

                if (eArquivo == -1) {
                    int eDiretorio = procuraFilho(HD, aux, nomeArquivo);

                    if (eDiretorio == -1) {
                        return "Arquivo ou Diretorio nao existe";
                    } else {
                        caminhoNovo = caminho;

                        aux = buscaCaminho(HD, caminhoNovo, dirAtual);
                        atualizaPermissaoDir(HD, eDiretorio, permissao);

                        result = "Permissao do Diretorio alterada com sucesso";
                        return result;
                    }
                }

                int PA = procuraArquivo(HD, aux, nomeArquivo);

                if (PA == -1) {
                    return "Arquivo nao existe";
                }

                atualizaPermissaoArquivo(HD, eArquivo, permissao);
                result = "Permissao alterada com sucesso";
            }

        } else {
            String mod = partes[0];
            permissao = partes[1];
            caminho = partes[2];

            if (mod.equals("-r") || mod.equals("-R")) {
                int aux2 = buscaCaminho(HD, caminho, dirAtual);

                if (aux2 == -1) {
                    return "Diretorio nao existe";
                } else {
                    atualizaPermissaoGeral(HD, aux2, permissao);
                }

                result = "Permissao alterada com sucesso";

            } else {
                return "Parametros invalidos";
            }

        }

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

        String texto = parameters;

        if (texto.equals("")) {
            return "Nome do arquivo nao pode ser vazio";
        }

        String[] partes = texto.split(" ", 2);

        String nomeArquivo;
        String conteudoArquivo;

        // Verificando as partes separadas
        if (partes.length >= 2) {
            nomeArquivo = partes[0];
            conteudoArquivo = partes[1];
        } else {

            nomeArquivo = partes[0];
            conteudoArquivo = "";

        }

        int ultimaBarraIndex = texto.lastIndexOf("/");

        if (ultimaBarraIndex >= 0) {

            String primeiraParte = texto.substring(0, ultimaBarraIndex);
            String segundaParte = texto.substring(ultimaBarraIndex + 1);

            int firstSpaceIndex = segundaParte.indexOf(' ');

            if (firstSpaceIndex != -1) {
                nomeArquivo = segundaParte.substring(0, firstSpaceIndex);
                conteudoArquivo = segundaParte.substring(firstSpaceIndex + 1);

            } else {
                nomeArquivo = segundaParte;
                conteudoArquivo = "";
            }

            if (ultimaBarraIndex == 0) {
                primeiraParte = "/";
            }

            String caminho = primeiraParte;

            int aux = buscaCaminho(HD, caminho, dirAtual);

            if (aux == -1) {
                return "Diretorio nao existe";
            }

            // Verificando se o nome do arquivo é válido
            if (!nomeArquivo.matches("^[a-zA-Z0-9].*")) {
                return "Nome do arquivo invalido";
            }

            int PA = procuraArquivo(HD, aux, nomeArquivo);
            int AC = arquivocheio(HD, aux);
            int PF = procuraFilho(HD, aux, nomeArquivo);

            if (AC == -1) {
                return "Quantidade de arquivos cheio";
            } else {
                if (PA == -1 && PF == -1) {
                    int posicao = procuraPosicaoVaziaHD(HD);
                    if (posicao == -10) {
                        return "HD cheio";
                    }

                    String ArquivoCriado = criaArquivo(nomeArquivo, aux, conteudoArquivo);
                    salvaNoHD(HD, ArquivoCriado, posicao);
                    atualizaPaiArquivo(HD, aux, posicao);

                    result = "Arquivo criado";

                } else {
                    result = "Arquivo com esse nome ja existe";
                }
            }

        } else {

            int aux = dirAtual;

            // Verificando se o nome do arquivo é válido
            if (!nomeArquivo.matches("^[a-zA-Z0-9].*")) {
                return "Nome do arquivo invalido";
            }

            int PA = procuraArquivo(HD, aux, nomeArquivo);
            int AC = arquivocheio(HD, aux);
            int PF = procuraFilho(HD, aux, nomeArquivo);

            if (AC == -1) {
                return "Quantidade de arquivos cheio";
            } else {
                if (PA == -1 && PF == -1) {
                    int posicao = procuraPosicaoVaziaHD(HD);
                    if (posicao == -10) {
                        return "HD cheio";
                    }

                    String ArquivoCriado = criaArquivo(nomeArquivo, aux, conteudoArquivo);
                    salvaNoHD(HD, ArquivoCriado, posicao);
                    atualizaPaiArquivo(HD, aux, posicao);

                    result = "Arquivo criado";

                } else {
                    result = "Arquivo com esse nome ja existe";
                }
            }

        }

        // fim da implementacao do aluno
        return result;
    }

    public String cat(String parameters) {
        // variavel result deverah conter o que vai ser impresso na tela apos comando do
        // usuário
        String result = "";
        System.out.println("Chamada de Sistema: cat");
        System.out.println("\tParametros: " + parameters);

        int ultimaBarraIndex = parameters.lastIndexOf("/");

        String caminho = "";
        String nomeArquivo = "";
        int aux = dirAtual;

        if (ultimaBarraIndex >= 0) {
            if (ultimaBarraIndex == 0) {
                caminho = "/";
                nomeArquivo = parameters.substring(ultimaBarraIndex + 1);
            } else {
                caminho = parameters.substring(0, ultimaBarraIndex);
                nomeArquivo = parameters.substring(ultimaBarraIndex + 1);
            }

        } else {
            caminho = "";
            nomeArquivo = parameters;

        }

        if (caminho.equals("")) {
            aux = dirAtual;
        } else {
            aux = buscaCaminho(HD, caminho, dirAtual);
        }

        if (aux == -1) {
            return "Diretorio nao existe";
        }

        int PA = procuraArquivo(HD, aux, nomeArquivo);

        if (PA == -1) {
            return "Arquivo nao existe";
        } else {
            String resultado = lerHD(HD, PA, 512);
            String conteudo = resultado.substring(112, 512).replaceAll("\\s+$", "");

            result = conteudo;
        }

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
        String version = "0.7";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

    // Funções 100% implementadas
    // info
    // cd
    // ls
    // cat
    // mkdir
    // mkdir a/../b/../c/../d/../a/Alberto/../../b/Betania/../../c/Cristina/../../d/Damaris/../../Rebeca/../Maria/../Bea
    // createfile
    // chmod

    // Funções em andamento


    // Funções não feitas
    // rmdir
    // cp
    // mv
    // rm
    // batch
    // dump

}
