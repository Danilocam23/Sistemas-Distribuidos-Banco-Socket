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
    int maximoConexiones = 10; // Maximo de conexiones simultaneas
    //Enviar datos
    DataOutputStream salida;
    //Guardar la infromacion de entrada
    BufferedReader entrada;
    

    public void iniciar() {
        try {
           
             server = new ServerSocket(puerto,maximoConexiones);
            while (true) { 
            
            socket = new Socket();
             socket = server.accept();
            // se configura la entrada y se optine el canal de la conexion.
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensaje = entrada.readLine();
            Operaciones op = new Operaciones();
            
            
            System.out.println(mensaje);
            //retorno de la informacion
            salida = new DataOutputStream(socket.getOutputStream());
            salida.writeUTF(op.CrecionObjeto(mensaje));
            
            //cerrar la conexion
            
            socket.close();
          }
            
            
        } catch (IOException  ex) {
            
        }finally{
         try {
                socket.close();
                server.close();
            } catch (IOException ex) {
                
            }
        }
    }

    
   
}
