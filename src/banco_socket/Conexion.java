/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banco_socket;

/**
 *
 * @author Danilo Camacho
 */
import java.net.*;
import java.io.*;
public class Conexion {
    //Instanciar Objetos
    ServerSocket server;
    Socket socket;
    //Variables Genericas
        //Puerto de la maquina que se va utilizar
        int puerto = 9000;
        //Enviar datos
        DataOutputStream salida;
        //Guardar la infromacion de entrada
        BufferedReader entrada;

    public void iniciar() {
        
    }
    
}
