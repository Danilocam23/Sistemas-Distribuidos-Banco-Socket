/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banco_socket;

/**
 *
 * @author xcojcama
 */
public class Operaciones {

    public String CrecionObjeto(String Datos) {

        String[] arrayColores = Datos.split(",");

        switch (arrayColores[0].toString()) {
            case "1":
                String numero = null;
                String prop = null;
                String din = null;

                switch (arrayColores[1].toString()) {
                    case "crear":
                        numero = arrayColores[2].toString().trim();
                        prop = arrayColores[3].toString().trim();
                        din = arrayColores[4].toString().trim();
                        return numero +" " + prop+" " + din +" ";
                    case "borrar":
                        numero = arrayColores[2].toString().trim();
                        return numero;
                    case "modificar":
                        numero = arrayColores[2].toString().trim();
                        prop = arrayColores[3].toString().trim();
                         return numero +" " + prop;
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
