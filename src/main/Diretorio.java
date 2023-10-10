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

    // Aqui eu procuro e devolvo o diretório que eu procuro
    public Diretorio caminhos(String caminho, Diretorio raiz, Diretorio atual) {

        Diretorio aux;

        // se o caminho for só / já retorna a raiz (fiz isso pra debugar o ls com
        // caminho)
        if (caminho.equals("/")) {
            return raiz;
        }

        // se o primeiro for / quer dizer que começa na raiz se nao começa no atual
        // diretorio
        if (caminho.startsWith("/")) {
            aux = raiz;
            caminho = caminho.substring(1);

        } else {
            aux = atual;
        }

        // aqui reparte o caminho pela /
        String[] partes = caminho.split("/");

        for (String parte : partes) {

            // se achar a parte fica true
            boolean achou = false;
            if (parte.equals(".")) {
                achou = true;
            }
            // o .. volta pro pai
            else if (parte.equals("..")) {
                aux = aux.pai;
                achou = true;
            }

            else {
                // esse confere os filhos
                for (Diretorio filhos : aux.filho) {
                    if (filhos.getNome().equals(parte)) {
                        aux = filhos;
                        achou = true;
                        break;
                    }
                }

            }

            // se não achou é porque o diretório não existe
            if (!achou) {
                return null;
            }

        }

        System.out.println("Diretorio: " + aux.getNome());
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
            this.arquivo = new ArrayList<>();

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
                        novo.arquivo = new ArrayList<>();

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

    public void adicionaArquivo(Arquivo arquivo, Diretorio pai) {

        pai.arquivo.add(arquivo);

    }

    // Função do codigo LS

    public String ls(String parameters, Diretorio atual, Diretorio raiz) {

        StringBuilder stringBuilder = new StringBuilder();

        // Aqui é o Ls sem parametros

        if (parameters.equals("")) {

            for (Diretorio Dfilho : filho) {
                stringBuilder.append(Dfilho.getNome() + " ");
            }
            for (Arquivo Darquivo : arquivo) {
                stringBuilder.append(Darquivo.getNome() + " ");
            }

            if (stringBuilder.toString().isEmpty()) {
                stringBuilder.append("Não existe pastas nem arquivos");
            }
        }

        else {
            String[] partes = parameters.split(" ");

            String nomeArquivo;
            String caminho;

            // Verificando as partes separadas
            if (partes.length >= 2) {
                nomeArquivo = partes[0];
                caminho = partes[1];

                System.out.println("Primeira parte: " + nomeArquivo);
                System.out.println("Segunda parte: " + caminho);

                Diretorio aux = new Diretorio();
                aux = aux.caminhos(caminho, raiz, atual);

                if (aux == null) {
                    return "Diretorio não existente";
                } else {
                    System.out.println("Nome Diretorio: " + aux.getNome());
                }

                for (Diretorio Dfilho : aux.filho) {

                    stringBuilder.append(Dfilho.getPermissao() + "\t");
                    stringBuilder.append(Dfilho.getDataHoraAtual() + "\t");
                    stringBuilder.append(Dfilho.getNome() + "\t");
                    stringBuilder.append("\n");
                }
                for (Arquivo Darquivo : aux.arquivo) {

                    stringBuilder.append(Darquivo.getPermissao() + "\t");
                    stringBuilder.append(Darquivo.getDataHoraAtual() + "\t");
                    stringBuilder.append(Darquivo.getNome() + "\t");
                    stringBuilder.append("\n");
                }

            } else {

                if (parameters.equals("-l")) {

                    Diretorio aux = new Diretorio();

                    for (Diretorio Dfilho : aux.filho) {
                        stringBuilder.append(Dfilho.getPermissao() + "\t");
                        stringBuilder.append(Dfilho.getDataHoraAtual() + "\t");
                        stringBuilder.append(Dfilho.getNome() + "\t");
                        stringBuilder.append("\n");
                    }
                    for (Arquivo Darquivo : aux.arquivo) {

                        stringBuilder.append(Darquivo.getPermissao() + "\t");
                        stringBuilder.append(Darquivo.getDataHoraAtual() + "\t");
                        stringBuilder.append(Darquivo.getNome() + "\t");
                        stringBuilder.append("\n");
                    }

                } else {
                    Diretorio aux = new Diretorio();
                    aux = aux.caminhos(parameters, raiz, atual);

                    if (aux == null) {
                        return "Diretorio não existente";
                    } else {
                        System.out.println("Nome Diretorio: " + aux.getNome());
                    }

                    for (Diretorio Dfilho : aux.filho) {
                        stringBuilder.append(Dfilho.getNome() + "\t");
                        stringBuilder.append("\n");
                    }
                    for (Arquivo Darquivo : aux.arquivo) {

                        stringBuilder.append(Darquivo.getNome() + "\t");
                        stringBuilder.append("\n");
                    }
                }

            }

        }

        if (stringBuilder.toString().isEmpty()) {
            stringBuilder.append("Não existe pastas nem arquivos");
        }

        return stringBuilder.toString();

    }

    public String cat(String parameters, Diretorio raiz, Diretorio atual) {
        String[] partes = parameters.split("/");
        String caminho = "";
        String nomeArquivo = "";
        Diretorio aux;

        if (partes.length >= 2) {
            // Obtém o índice da última barra
            int lastBarIndex = parameters.lastIndexOf("/");

            // Verifica se encontrou a última barra
            if (lastBarIndex >= 0) {
                // Obtém o nome do arquivo
                nomeArquivo = partes[partes.length - 1];
                // Obtém o conteúdo antes da última barra
                caminho = parameters.substring(0, lastBarIndex);

                aux = caminhos(caminho, raiz, atual);

                if (aux == null) {
                    return "Diretório não existente";
                }

                else {

                    for (Arquivo arquivos : aux.arquivo) {
                        if (nomeArquivo.equals(arquivos.getNome())) {

                            return arquivos.getConteudo();
                        }
                    }

                }
            }
        } else {
            nomeArquivo = parameters;
            for (Arquivo arquivos : atual.arquivo) {
                if (nomeArquivo.equals(arquivos.getNome())) {
                    return arquivos.getConteudo();
                }
            }
        }

        return "Arquivo não existe";

    }

    public String converte(String permissao) {
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

    public String mPermissao1(String p, Diretorio a) {

        String perm = converte(p);

        if (a == null) {
            return "";
        }

        for (Diretorio child : a.filho) {
            mPermissao1(p, child);
        }

        if (a.arquivo.isEmpty()) {

        } else {
            for (Arquivo ar : a.arquivo) {
                ar.setPermissao("a" + perm);
            }
        }

        if (a.filho.isEmpty()) {

        } else {
            for (Diretorio dir : a.filho) {
                dir.permissao = "d" + perm;
            }
        }

        return "";

    }

    public String chmod(String parameters, Diretorio raiz, Diretorio atual) {

        String[] partes = parameters.split(" ");
        String caminho;
        Diretorio aux = new Diretorio();

        for (String parte : partes) {
            System.out.println(parte);

        }

        if (partes.length <= 1) {
            return "Erro Parametros insuficientes";

        } else if (partes.length >= 2) {
            if (partes[0].equals("-r") || partes[0].equals("-R")) {
                caminho = partes[2];

                aux = aux.caminhos(caminho, raiz, atual);

                if (aux.equals(null)) {
                    return "Error";
                } else {

                    aux.permissao = "d" + converte(partes[1]);
                    mPermissao1(partes[1], aux);

                }

            } else {

                aux = aux.caminhos(partes[1], raiz, atual);

                if (aux.equals(null)) {
                    return "Error";
                } else {

                    aux.permissao = "d" + converte(partes[1]);
                    mPermissao1(partes[0], aux);

                }

            }

        }

        return "";
    }

    public String rm(String parameters, Diretorio raiz, Diretorio atual) {

        String[] partes = parameters.split(" ");

        String func;
        String caminho;

        if (partes.length == 1) {

            int lastBarIndex = parameters.lastIndexOf("/");
            String nomeArquivo = parameters.substring(lastBarIndex+1);;

            // Obtém o conteúdo antes da última barra
            caminho = parameters.substring(0, lastBarIndex);

            System.out.println("Caminho:" + caminho);
            System.out.println("Nome Arquivo:" + nomeArquivo);

            Diretorio aux = new Diretorio();
            aux = aux.caminhos(caminho, raiz, atual);

            if (aux == null) {
                return "Diretorio não existente";
            } else {
                System.out.println("Nome Diretorio: " + aux.getNome());
            }

            for(Diretorio filhos: filho){
                System.out.println(filhos);
            }

        }

        // Verificando as partes separadas
        if (partes.length >= 2) {
            func = partes[0];
            caminho = partes[1];

            System.out.println("Primeira parte: " + func);
            System.out.println("Caminho: " + caminho);

            Diretorio aux = new Diretorio();
            aux = aux.caminhos(caminho, raiz, atual);

            if (aux == null) {
                return "Diretorio não existente";
            } else {
                System.out.println("Nome Diretorio: " + aux.getNome());
            }
        }

        return "";

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

    public List<Arquivo> getArquivo() {
        return arquivo;
    }

    public void setArquivo(List<Arquivo> arquivo) {
        this.arquivo = arquivo;
    }

    public LocalDateTime getDataHoraAtual() {
        return dataHoraAtual;
    }

    public void setDataHoraAtual(LocalDateTime dataHoraAtual) {
        this.dataHoraAtual = dataHoraAtual;
    }

}
