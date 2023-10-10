package main;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import operatingSystem.fileSystem.FileSytemSimulator;

/**
 * Funcao principal do sistema.
 */
public class Main {

    public static void main(String[] args) {
        //instanciando o kernel definido pelo aluno
        MyKernel1 k = new MyKernel1();
        //instanciando o simulador de Sistema de Arquivos
        new FileSytemSimulator(k).setVisible(true);
    }

}
