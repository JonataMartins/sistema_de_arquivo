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
import java.util.Arrays;
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

    public static void salvaNoHD(HardDisk hd, String texto, int posicao) {
        boolean[] bits = stringToBinaryArray(texto);

        for (int i = 0; i < bits.length; i++) {
            hd.setBitDaPosicao(bits[i], posicao * 512 * 8 + i);
        }
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

    public static void apagaArquivos(HardDisk hd, int arquivo) {
        String resultado = lerHD(hd, arquivo, 512);

        System.out.println("Apagando Arquivo ...");

        String estado = resultado.substring(0, 1);
        String nome = resultado.substring(1, 87);
        String pai = resultado.substring(87, 97);
        String data = resultado.substring(97, 109);
        String permissao = resultado.substring(109, 112);
        String conteudo = resultado.substring(112, 512);

        int p = Integer.parseInt(pai.replaceAll("\\s+", ""));

        String paiArquivo = lerHD(hd, p, 512);

        String estadoPAI = paiArquivo.substring(0, 1);
        String nomePAI = paiArquivo.substring(1, 87);
        String paiPAI = paiArquivo.substring(87, 97);
        String dataPAI = paiArquivo.substring(497, 509);
        String permissaoPAI = paiArquivo.substring(509, 512);

        String[] dirArquivos = new String[20];
        String[] dirFilhos = new String[20];

        for (int i = 0; i < 20; i++) {
            dirArquivos[i] = paiArquivo.substring(297 + (i * 10), 307 + (i * 10));
            dirFilhos[i] = paiArquivo.substring(97 + (i * 10), 107 + (i * 10));
        }

        boolean tem = false;

        for (int i = 0; i < 20; i++) {
            if (dirArquivos[i].replaceAll("\\s+", "").equals(Integer.toString(arquivo))) {
                dirArquivos[i] = String.format("%-" + 10 + "s", "");
                tem = true;
                break;
            }
        }

        if (tem) {
            StringBuilder arquivoPAI = new StringBuilder();
            arquivoPAI.append(estadoPAI);
            arquivoPAI.append(nomePAI);
            arquivoPAI.append(paiPAI);
            for (int i = 0; i < 20; i++) {
                arquivoPAI.append(dirFilhos[i]);
            }
            for (int i = 0; i < 20; i++) {
                arquivoPAI.append(dirArquivos[i]);
            }

            arquivoPAI.append(dataPAI);
            arquivoPAI.append(permissaoPAI);

            String converte = arquivoPAI.toString();
            salvaNoHD(hd, converte, p);
        }

        estado = " ";
        nome = " ";
        pai = " ";
        data = " ";
        permissao = " ";
        conteudo = " ";

        salvaNoHD(hd, estado + nome + pai + data + permissao + conteudo, arquivo);

    }

    public static void apagaDir(HardDisk hd, int diretorio) {
        String resultado = lerHD(hd, diretorio, 512);

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

        int p = Integer.parseInt(pai.replaceAll("\\s+", ""));

        String paiArquivo = lerHD(hd, p, 512);

        String estadoPAI = paiArquivo.substring(0, 1);
        String nomePAI = paiArquivo.substring(1, 87);
        String paiPAI = paiArquivo.substring(87, 97);
        String dataPAI = paiArquivo.substring(497, 509);
        String permissaoPAI = paiArquivo.substring(509, 512);

        String[] dirArquivosPAI = new String[20];
        String[] dirFilhosPAI = new String[20];

        for (int i = 0; i < 20; i++) {
            dirArquivosPAI[i] = paiArquivo.substring(297 + (i * 10), 307 + (i * 10));
            dirFilhosPAI[i] = paiArquivo.substring(97 + (i * 10), 107 + (i * 10));
        }

        boolean tem = false;

        for (int i = 0; i < 20; i++) {
            if (dirFilhosPAI[i].replaceAll("\\s+", "").equals(Integer.toString(diretorio))) {
                dirFilhosPAI[i] = String.format("%-" + 10 + "s", "");
                tem = true;
                break;
            }
        }

        if (tem) {

            StringBuilder diretorioPAI = new StringBuilder();
            diretorioPAI.append(estadoPAI);
            diretorioPAI.append(nomePAI);
            diretorioPAI.append(paiPAI);
            for (int i = 0; i < 20; i++) {
                diretorioPAI.append(dirFilhosPAI[i]);
            }
            for (int i = 0; i < 20; i++) {
                diretorioPAI.append(dirArquivosPAI[i]);
            }

            diretorioPAI.append(dataPAI);
            diretorioPAI.append(permissaoPAI);

            String converte = diretorioPAI.toString();
            salvaNoHD(hd, converte, p);
        }

        estado = " ";
        nome = " ";
        pai = " ";

        StringBuilder dir = new StringBuilder();
        dir.append(estado);
        dir.append(nome);
        dir.append(pai);
        for (int i = 0; i < 20; i++) {
            dirFilhos[i] = " ";
            dir.append(dirFilhos[i]);
        }
        for (int i = 0; i < 20; i++) {
            dirArquivos[i] = " ";
            dir.append(dirArquivos[i]);
        }

        data = " ";
        dir.append(data);
        permissao = " ";
        dir.append(permissao);

        String converte = dir.toString();
        salvaNoHD(hd, converte, diretorio);

    }

    public static void apagaGeral(HardDisk hd, int dirAtual, int original) {
        if (existeFilho(hd, dirAtual) == 1) {// 1 é pq existe algum filho
            int[] todosFilhos = new int[20];
            todosFilhos = retornaFilho(hd, dirAtual);

            for (int i = 0; i < 20; i++) {
                if (todosFilhos[i] != -1) {
                    apagaGeral(hd, todosFilhos[i], original);
                }
            }

            for (int i = 0; i < 20; i++) {
                int[] todosArquivos = retornaArquivo(hd, dirAtual); // Modificado para retornar arquivos de dirAtual

                if (todosArquivos[i] != -1) {
                    apagaArquivos(hd, todosArquivos[i]);
                }
            }

            int[] tFilhos = retornaFilho(hd, dirAtual); // Modificado para retornar filhos de dirAtual
            for (int i = 0; i < 20; i++) {
                if (tFilhos[i] != -1) {

                    apagaDir(hd, tFilhos[i]);
                }
            }

            apagaDir(hd, original);
        } else {

        }

    }

    public static void atualizaPermissaoGeral(HardDisk hd, int dirAtual, String permissao) {

        if (existeFilho(hd, dirAtual) == 1) {// 1 é pq existe algum filho
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

        String resto = resultado.substring(0, 509);

        String permissao = perm;

        salvaNoHD(hd, resto + permissao, dirAtual);

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
                System.out.println("Posição: " + i);
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

    public static String mvDir(HardDisk HD, int cam1, int cam2, String arquivo1, String arquivo2) {

        int mv1 = procuraFilho(HD, cam1, arquivo1);
        int mv2 = procuraFilho(HD, cam2, arquivo2);

        int cheio = filhocheio(HD, mv2);

        if (cheio == -1) {
            return "O Diretorio de destino está cheio (nada foi Movido)";
        }

        String resultado1 = lerHD(HD, mv1, 512);

        String estado1 = resultado1.substring(0, 1);
        String nome1 = resultado1.substring(1, 87);
        String pai = resultado1.substring(87, 97);
        String resto = resultado1.substring(97, 512);

        if (!estado1.equals("d")) {
            return "Esse comando só é válido para Diretorios (nada foi Movido)";
        }

        int PF = procuraFilho(HD, cam2, nome1);
        int PF2 = procuraArquivo(HD, cam2, nome1);

        if (PF != -1 || PF2 != -1) {
            return "Já existe um diretorio ou um arquivo com esse nome no caminho de destino (nada foi Movido)";
        }

        String paiDoMV = lerHD(HD, Integer.parseInt(pai.replaceAll("\\s+", "")), 512);

        String estadoPMV = paiDoMV.substring(0, 1);
        String nomePMV = paiDoMV.substring(1, 87);
        String paiPMV = paiDoMV.substring(87, 97);
        String dirFilhos[] = new String[20];
        String resto2 = paiDoMV.substring(297, 512);

        System.out.println("nomePMV: " + nomePMV);

        for (int i = 0; i < 20; i++) {
            dirFilhos[i] = paiDoMV.substring(97 + (i * 10), 107 + (i * 10));

            if (dirFilhos[i].replaceAll("\\s+", "").equals(Integer.toString(mv1))) {
                dirFilhos[i] = String.format("%-" + 10 + "s", "");
            }
        }

        StringBuilder painovo = new StringBuilder();

        painovo.append(estadoPMV);
        painovo.append(nomePMV);
        painovo.append(paiPMV);

        for (int i = 0; i < 20; i++) {
            painovo.append(dirFilhos[i]);
        }

        painovo.append(resto2);

        String converte = painovo.toString();

        salvaNoHD(HD, converte, Integer.parseInt(pai.replaceAll("\\s+", "")));

        pai = String.format("%-" + 10 + "s", mv2);

        StringBuilder sb = new StringBuilder();

        sb.append(estado1);
        sb.append(nome1);
        sb.append(pai);
        sb.append(resto);

        String novo = sb.toString();
        salvaNoHD(HD, novo, mv1);

        atualizaPaiDir(HD, mv2, mv1);

        return "Movido com sucesso";
    }

    public static String mvArq(HardDisk HD, int cam1, int cam2, String arquivo1, String arquivo2) {

        int mv1 = procuraArquivo(HD, cam1, arquivo1);
        int mv2 = procuraFilho(HD, cam2, arquivo2);

        int cheio = arquivocheio(HD, mv2);

        if (cheio == -1) {
            return "O Diretorio de destino está cheio (nada foi Movido)";
        }

        String resultado1 = lerHD(HD, mv1, 512);

        String estado1 = resultado1.substring(0, 1);
        String nome1 = resultado1.substring(1, 87);
        String pai = resultado1.substring(87, 97);
        String resto = resultado1.substring(97, 512);

        if (!estado1.equals("a")) {
            return "Esse comando só é válido para Arquivos (nada foi Movido)";
        }

        int PF = procuraArquivo(HD, cam2, nome1);
        int PF2 = procuraFilho(HD, cam2, nome1);

        if (PF != -1 || PF2 != -1) {
            return "Já existe um diretorio ou arquivo com esse nome no caminho de destino (nada foi Movido)";
        }

        String paiDoMV = lerHD(HD, Integer.parseInt(pai.replaceAll("\\s+", "")), 512);

        String estadoPMV = paiDoMV.substring(0, 1);
        String nomePMV = paiDoMV.substring(1, 87);
        String paiPMV = paiDoMV.substring(87, 97);
        String dirDiretorios[] = new String[20];
        String dirArquivos[] = new String[20];
        String resto2 = paiDoMV.substring(497, 512);

        for (int i = 0; i < 20; i++) {
            dirDiretorios[i] = paiDoMV.substring(97 + (i * 10), 107 + (i * 10));
        }

        for (int i = 0; i < 20; i++) {
            dirArquivos[i] = paiDoMV.substring(297 + (i * 10), 307 + (i * 10));

            if (dirArquivos[i].replaceAll("\\s+", "").equals(Integer.toString(mv1))) {
                dirArquivos[i] = String.format("%-" + 10 + "s", "");
            }
        }

        StringBuilder painovo = new StringBuilder();

        painovo.append(estadoPMV);
        painovo.append(nomePMV);
        painovo.append(paiPMV);

        for (int i = 0; i < 20; i++) {
            painovo.append(dirDiretorios[i]);
        }

        for (int i = 0; i < 20; i++) {
            painovo.append(dirArquivos[i]);
        }

        painovo.append(resto2);

        System.out.println("painovo: " + painovo.toString());

        String converte = painovo.toString();

        salvaNoHD(HD, converte, Integer.parseInt(pai.replaceAll("\\s+", "")));

        pai = String.format("%-" + 10 + "s", mv2);

        StringBuilder sb = new StringBuilder();

        sb.append(estado1);
        sb.append(nome1);
        sb.append(pai);
        sb.append(resto);

        String novo = sb.toString();

        salvaNoHD(HD, novo, mv1);

        atualizaPaiArquivo(HD, mv2, mv1);

        return "Movido com sucesso";
    }

    public static String cpArq(HardDisk HD, int cam1, int cam2, String arquivo1, String arquivo2) {

        int mv1 = procuraArquivo(HD, cam1, arquivo1);
        int mv2 = procuraFilho(HD, cam2, arquivo2);

        int cheio = arquivocheio(HD, mv2);

        if (cheio == -1) {
            return "O Diretorio de destino está cheio (nada foi Copiado)";
        }

        String resultado1 = lerHD(HD, mv1, 512);

        String estado1 = resultado1.substring(0, 1);
        String nome1 = resultado1.substring(1, 87);
        String pai = resultado1.substring(87, 97);
        String resto = resultado1.substring(97, 512);

        if (!estado1.equals("a")) {
            return "Esse comando só é válido para Arquivos (nada foi Copiado)";
        }

        int PF = procuraArquivo(HD, cam2, nome1);
        int PF2 = procuraFilho(HD, cam2, nome1);

        if (PF != -1 || PF2 != -1) {
            return "Já existe um diretorio ou arquivo com esse nome no caminho de destino (nada foi Copiado)";
        }

        pai = String.format("%-" + 10 + "s", mv2);

        StringBuilder sb = new StringBuilder();

        sb.append(estado1);
        sb.append(nome1);
        sb.append(pai);
        sb.append(resto);

        String novo = sb.toString();

        int proc = procuraPosicaoVaziaHD(HD);

        salvaNoHD(HD, novo, proc);

        atualizaPaiArquivo(HD, mv2, proc);

        return "Copiado com sucesso";
    }

    public static String cpDir(HardDisk HD, int cam1, int cam2, String arquivo1, String arquivo2) {

        int mv1 = procuraFilho(HD, cam1, arquivo1);
        int mv2 = procuraFilho(HD, cam2, arquivo2);

        int cheio = filhocheio(HD, mv2);

        if(procuraFilho(HD, mv2, arquivo1) != -1){
            return "já existe algo com esse nome";
        }

        if (cheio == -1) {
            return "O Diretorio de destino está cheio (nada foi Copiado)";
        }

        String resultado1 = lerHD(HD, mv1, 512);

        String estado1 = resultado1.substring(0, 1);
        String nome1 = resultado1.substring(1, 87);
        String pai = resultado1.substring(87, 97);
        String data = resultado1.substring(497, 509);
        String permissao = resultado1.substring(509, 512);

        String[] dirFilhos = new String[20];
        String[] dirArquivos = new String[20];

        for (int i = 0; i < 20; i++) {
            dirFilhos[i] = resultado1.substring(97 + (i * 10), 107 + (i * 10));
            dirArquivos[i] = resultado1.substring(297 + (i * 10), 307 + (i * 10));

            dirArquivos[i] = String.format("%-" + 10 + "s", "");
            dirFilhos[i] = String.format("%-" + 10 + "s", "");
        }

        if (!estado1.equals("d")) {
            return "Esse comando só é válido para Diretorios (nada foi Copiado)";
        }

        int PF = procuraFilho(HD, cam2, nome1);
        int PF2 = procuraArquivo(HD, cam2, nome1);

        if (PF != -1 || PF2 != -1) {
            return "Já existe um diretorio ou um arquivo com esse nome no caminho de destino (nada foi Copiado)";
        }

        pai = String.format("%-" + 10 + "s", mv2);

        StringBuilder sb = new StringBuilder();

        sb.append(estado1);
        sb.append(nome1);
        sb.append(pai);
        
        for (int i = 0; i < 20; i++) {
            sb.append(dirFilhos[i]);
        }

        for (int i = 0; i < 20; i++) {
            sb.append(dirArquivos[i]);
        }

        sb.append(data);
        sb.append(permissao);

        String novo = sb.toString();

        int proc = procuraPosicaoVaziaHD(HD);

        salvaNoHD(HD, novo, proc);

        atualizaPaiDir(HD, mv2, proc);


        return "Copiado com sucesso";
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

        //exibeHD(HD, dirAtual);
        //fim da implementacao do aluno
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
                    if (PF != -1) {
                        aux = PF;
                    } else {
                        return "Quantidade de diretorio cheio";
                    }

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
        if (parameters == "") {
            return "Indique um caminho (nada foi apagado)";
        }
        int aux = buscaCaminho(HD, parameters, dirAtual);
        if (retornaEstado(HD, aux) == "a") {
            return "Esse comando não funciona em Arquivos (nada foi apagado)";
        }
        if (aux == -1) {
            return "Diretorio não existe (nada foi apagado)";
        }

        int dir = existeFilho(HD, aux);
        int arq = existeArquivo(HD, aux);

        if (dir == 1 || arq == 1) {
            return "Diretorio deve estar vazio para poder ser apagado";
        }

        apagaDir(HD, aux);

        result = "Diretorio apagado com sucesso";

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
        if (parameters == "") {
            return "Indique os dois caminho (nada foi copiado)";
        }

        String[] partes = parameters.split(" ");

        if (partes.length != 2) {
            return "Parametros invalidos (nada foi copiado)";
        }

        if (partes[0].equals("/")) {
            return "Não é possivel copiar a raiz (nada foi copiado)";
        }

        String caminho1, caminho2, arquivo1, arquivo2;

        int posicao = partes[0].indexOf("/");
        if (posicao > 1) {
            int ultimaBarraIndex = partes[0].lastIndexOf("/");
            caminho1 = partes[0].substring(0, ultimaBarraIndex);
            arquivo1 = partes[0].substring(ultimaBarraIndex + 1);
        } else {

            if (partes[0].startsWith("/")) {
                caminho1 = "/";
                arquivo1 = partes[0].substring(1);
            } else {
                caminho1 = ".";
                arquivo1 = partes[0];
            }

        }

        posicao = partes[1].indexOf("/");
        if (posicao > 1) {
            int ultimaBarraIndex = partes[1].lastIndexOf("/");
            caminho2 = partes[1].substring(0, ultimaBarraIndex);
            arquivo2 = partes[1].substring(ultimaBarraIndex + 1);
        } else {
            if (partes[1].startsWith("/")) {
                caminho2 = "/";
                arquivo2 = partes[1].substring(1);
            } else {
                caminho2 = ".";
                arquivo2 = partes[1];
            }

        }

        int cam1 = buscaCaminho(HD, caminho1, dirAtual);
        int cam2 = buscaCaminho(HD, caminho2, dirAtual);

        if (cam1 == -1 || cam2 == -1) {
            return "Caminho não existe (nada foi copiado)";
        }

        int pa = procuraArquivo(HD, cam2, arquivo2);

        if (pa != -1) {
            return "o segundo caminho não pode ser um arquivo, nada foi copiado";
        }

        if (cam1 == cam2) {

            int diretorio = procuraFilho(HD, cam1, arquivo1);
            int arquivo = procuraArquivo(HD, cam1, arquivo1);

            if (diretorio == -1 && arquivo == -1) {
                return "O Arquivo ou diretorio não existe, nada foi copiado";
            }

            int filho;

            if (diretorio != -1) {
                filho = diretorio;

                int existe = procuraFilho(HD, cam2, arquivo2);
                int existe2 = procuraArquivo(HD, cam2, arquivo2);

                if (existe != -1 && existe2 != -1) {
                    return "Já existe um diretorio ou um arquivo com esse nome no caminho de destino (nada foi copiado)";
                }

                if (existe != -1 || existe2 != -1) {
                    if (existe != -1) {
                         result = cpDir(HD, cam1, cam2, arquivo1, arquivo2);
                    }

                    return result;
                }
            } else {
                filho = arquivo;

                int existe = procuraFilho(HD, cam2, arquivo2);
                int existe2 = procuraArquivo(HD, cam2, arquivo2);

                if (existe != -1 && existe2 != -1) {
                    return "Já existe um diretorio ou um arquivo com esse nome no caminho de destino (nada foi copiado)";
                }

                if (existe != -1 || existe2 != -1) {
                    if (existe != -1) {
                         result = cpArq(HD, cam1, cam2, arquivo1, arquivo2);
                        return result;
                    }

                    return "Não era pra isso ter acontecido, mas se aconteceu, nada foi copiado";

                }

            }

            String resultado = lerHD(HD, filho, 512);

            String estado = resultado.substring(0, 1);
            String nome = resultado.substring(1, 87);
            String resto = resultado.substring(87, 512);

            nome = String.format("%-" + 86 + "s", arquivo2);

            if (!nome.matches("^[a-zA-Z0-9]*")) {
                return "Nome invalido";
            }

            String novo = estado + nome + resto;

            salvaNoHD(HD, novo, filho);

            return "Nome alterado com sucesso";
        } else {

            int r = procuraFilho(HD, cam1, arquivo1);
            int l = procuraArquivo(HD, cam1, arquivo1);

            if (r == -1 && l == -1) {
                return "o arquivo ou diretório não existe, nada foi copiado";
            }

            if (r != -1) {
                result = cpDir(HD, cam1, cam2, arquivo1, arquivo2);
            }

            if (l != -1) {
                result = cpArq(HD, cam1, cam2, arquivo1, arquivo2);
            }

        }

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
        if (parameters == "") {
            return "Indique os dois caminho (nada foi Movido)";
        }

        String[] partes = parameters.split(" ");

        if (partes.length != 2) {
            return "Parametros invalidos (nada foi Movido)";
        }

        if (partes[0].equals("/")) {
            return "Não é possivel mover a raiz";
        }

        String caminho1, caminho2, arquivo1, arquivo2;

        int posicao = partes[0].indexOf("/");
        if (posicao > 1) {
            int ultimaBarraIndex = partes[0].lastIndexOf("/");
            caminho1 = partes[0].substring(0, ultimaBarraIndex);
            arquivo1 = partes[0].substring(ultimaBarraIndex + 1);
        } else {

            if (partes[0].startsWith("/")) {
                caminho1 = "/";
                arquivo1 = partes[0].substring(1);
            } else {
                caminho1 = ".";
                arquivo1 = partes[0];
            }

        }

        posicao = partes[1].indexOf("/");
        if (posicao > 1) {
            int ultimaBarraIndex = partes[1].lastIndexOf("/");
            caminho2 = partes[1].substring(0, ultimaBarraIndex);
            arquivo2 = partes[1].substring(ultimaBarraIndex + 1);
        } else {
            if (partes[1].startsWith("/")) {
                caminho2 = "/";
                arquivo2 = partes[1].substring(1);
            } else {
                caminho2 = ".";
                arquivo2 = partes[1];
            }

        }

        int cam1 = buscaCaminho(HD, caminho1, dirAtual);
        int cam2 = buscaCaminho(HD, caminho2, dirAtual);

        if (cam1 == -1 || cam2 == -1) {
            return "Caminho não existe (nada foi Movido)";
        }

        int pa = procuraArquivo(HD, cam2, arquivo2);

        if (pa != -1) {
            return "o segundo caminho não pode ser um arquivo, nada foi movido";
        }

        if (cam1 == cam2) {

            int diretorio = procuraFilho(HD, cam1, arquivo1);
            int arquivo = procuraArquivo(HD, cam1, arquivo1);

            if (diretorio == -1 && arquivo == -1) {
                return "O Arquivo ou diretorio não existe, nada foi movido";
            }

            int filho;

            if (diretorio != -1) {
                filho = diretorio;

                int existe = procuraFilho(HD, cam2, arquivo2);
                int existe2 = procuraArquivo(HD, cam2, arquivo2);

                if (existe != -1 && existe2 != -1) {
                    return "Já existe um diretorio ou um arquivo com esse nome no caminho de destino (nada foi Movido)";
                }

                if (existe != -1 || existe2 != -1) {
                    if (existe != -1) {
                        result = mvDir(HD, cam1, cam2, arquivo1, arquivo2);
                    }

                    return result;
                }
            } else {
                filho = arquivo;

                int existe = procuraFilho(HD, cam2, arquivo2);
                int existe2 = procuraArquivo(HD, cam2, arquivo2);

                if (existe != -1 && existe2 != -1) {
                    return "Já existe um diretorio ou um arquivo com esse nome no caminho de destino (nada foi Movido)";
                }

                if (existe != -1 || existe2 != -1) {
                    if (existe != -1) {
                        result = mvArq(HD, cam1, cam2, arquivo1, arquivo2);
                        return result;
                    }

                    return "Não era pra isso ter acontecido, mas se aconteceu, nada foi movido";

                }

            }

            String resultado = lerHD(HD, filho, 512);

            String estado = resultado.substring(0, 1);
            String nome = resultado.substring(1, 87);
            String resto = resultado.substring(87, 512);

            nome = String.format("%-" + 86 + "s", arquivo2);

            if (!nome.matches("^[a-zA-Z0-9]*")) {
                return "Nome invalido";
            }

            String novo = estado + nome + resto;

            salvaNoHD(HD, novo, filho);

            return "Nome alterado com sucesso";
        } else {

            int r = procuraFilho(HD, cam1, arquivo1);
            int l = procuraArquivo(HD, cam1, arquivo1);

            if (r == -1 && l == -1) {
                return "o arquivo ou diretório não existe, nada foi movido";
            }

            if (r != -1) {
                result = mvDir(HD, cam1, cam2, arquivo1, arquivo2);
            }

            if (l != -1) {
                result = mvArq(HD, cam1, cam2, arquivo1, arquivo2);
            }

        }

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

        String[] partes = parameters.split(" ");

        if (partes.length == 2) {
            String caminho = partes[1];
            String comando = partes[0];

            System.out.println("Comando: " + partes[0]);
            System.out.println(partes[1]);

            if (!comando.equals("-R")) {
                return "Parametros invalidos (nenhum Diretorio apagado)";
            }

            int aux = buscaCaminho(HD, caminho, dirAtual);

            System.out.println("aux: " + aux);

            if (retornaEstado(HD, aux).equals("a")) {
                return "Esse comando só é válido para Diretorios, para arquivos retire o -R (nenhum Diretorio apagado)";
            }

            if (aux == -1) {
                return "Diretorio não existe (nenhum Arquivo apagado)";
            }

            apagaGeral(HD, aux, aux);

        } else if (partes.length == 1) {

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
                caminho = ".";
                nomeArquivo = parameters;

            }

            if (caminho.equals("")) {
                aux = dirAtual;
            } else {
                aux = buscaCaminho(HD, caminho, dirAtual);
                if (aux == -1) {
                    return "Diretorio nao existe (nenhum Arquivo apagado)";
                }
            }

            if (retornaEstado(HD, aux).equals("d")) {
                return "Esse comando só é válido para Arquivos, para diretórios coloque o -R (nenhum Arquivo apagado)";
            }

            int Arquivo = procuraArquivo(HD, aux, nomeArquivo);

            if (Arquivo == -1) {
                return "Arquivo nao existe (nenhum Arquivo apagado)";
            }

            apagaArquivos(HD, Arquivo);
            return "Arquivo apagado com sucesso";
            // só arquivos

        } else {
            return "Parametros invalidos (nenhum Arquivo apagado)";
        }

        // fim da implementacao do aluno
        return result;
    }

    public String chmod(String parameters) {
        System.out.println("Chamada de Sistema: chmod");
        System.out.println("\tParametros: " + parameters);

        String[] partes = parameters.split(" ");

        if (partes.length < 2) {
            return "Erro: Parâmetros insuficientes";
        }

        String mod = partes[0];
        String permissao = partes[1];
        String caminho = partes[2];

        if (mod.equals("-r") || mod.equals("-R")) {
            int aux2 = buscaCaminho(HD, caminho, dirAtual);

            if (aux2 == -1) {
                return "Diretório não existe";
            }

            atualizaPermissaoGeral(HD, aux2, permissao);
            return "Permissão alterada com sucesso";
        } else if (partes.length == 3) {
            int aux = dirAtual;
            String[] caminhoPartes = caminho.split("/");
            String nomeArquivo = caminhoPartes[caminhoPartes.length - 1];
            String caminhoNovo = caminhoPartes.length > 1
                    ? String.join("/", Arrays.copyOf(caminhoPartes, caminhoPartes.length - 1))
                    : "";

            if (caminhoPartes.length == 1 && caminho.equals("-r") || caminho.equals("-R")) {
                return "Permissão inválida";
            }

            aux = buscaCaminho(HD, caminhoNovo, dirAtual);

            if (aux == -1) {
                return "Diretório não existe";
            }

            int eArquivo = procuraArquivo(HD, aux, nomeArquivo);
            int eDiretorio = procuraFilho(HD, aux, nomeArquivo);

            if (eArquivo == -1 && eDiretorio == -1) {
                return "Arquivo ou Diretório não existe";
            } else if (eDiretorio != -1) {
                caminhoNovo = caminho;
                aux = buscaCaminho(HD, caminhoNovo, dirAtual);
                atualizaPermissaoDir(HD, eDiretorio, permissao);
                return "Permissão do Diretório alterada com sucesso";
            } else {
                int PA = procuraArquivo(HD, aux, nomeArquivo);

                if (PA == -1) {
                    return "Arquivo não existe";
                }

                atualizaPermissaoArquivo(HD, eArquivo, permissao);
                return "Permissão alterada com sucesso";
            }
        } else {
            return "Parâmetros inválidos";
        }
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
        String version = "0.9";

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
    // mkdir a/../b/../c/../d/../a/Alberto/../../b/Betania/../../c/C/../../d/Damaris/../../Rebeca/../Maria/../Bea
    // createfile
    // chmod
    // rm
    // rmdir

    // Funções em andamento
    // mv
    // cp

    // Funções não feitas
    // batch
    // dump

}
