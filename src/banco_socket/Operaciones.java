/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banco_socket;

import banco_DB.OperacionesDB;

/**
 *
 * @author xcojcama
 */
public class Operaciones {

    public String CrecionObjeto(String Datos) {

        OperacionesDB odb = new OperacionesDB();
        String[] arrayColores = Datos.split(",");

        switch (arrayColores[0].toString()) {
            case "1":
                String numero = null;
                String propNom = null;
                String propApe = null;
                String din = null;

                switch (arrayColores[1].toString()) {
                    case "crear":
                        propNom = arrayColores[3].toString().trim();
                        propApe = arrayColores[4].toString().trim();
                        numero = arrayColores[2].toString().trim();
                        din = arrayColores[5].toString().trim();
                        int IdUsuario = odb.CrearUsuario(propNom, propApe);
                        int IdCuenta = odb.CrearCuenta(IdUsuario, numero);
                        odb.CrearDinero(IdCuenta,din);

                        
                        return numero + " " + propNom + " " + propApe + " " + din + " " + IdUsuario;
                    case "borrar":
                        numero = arrayColores[2].toString().trim();
                        return numero;
                    case "modificar":
                        numero = arrayColores[2].toString().trim();
                        propNom = arrayColores[3].toString().trim();
                        propApe = arrayColores[4].toString().trim();
                        return numero + " " + propNom + " " + propApe;
                    default:
                        return "Error";
                }
            case "2":
                return "Danilo2";
            case "3":
                return "Danilo3";
            default:
                return "Error";
        }

    }
}
