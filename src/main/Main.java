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
        MyKernel2 k = new MyKernel2();
        //instanciando o simulador de Sistema de Arquivos
        new FileSytemSimulator(k).setVisible(true);
    }

}
