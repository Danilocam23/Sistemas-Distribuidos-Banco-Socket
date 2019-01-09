/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banco_socket;

import banco_DB.OperacionesDB;

/**
 *
 * @author Danilo Camacho
 */
public class Operaciones {

    public String CrecionObjeto(String Datos) {

        OperacionesDB odb = new OperacionesDB();
        String[] arrayColores = Datos.split(",");

        switch (arrayColores[0]) {
            case "1":
                String numero;
                String propNom;
                String propApe;
                String din;
                String msg;

                switch (arrayColores[1]) {
                    case "crear":
                        propNom = arrayColores[3].trim();
                        propApe = arrayColores[4].trim();
                        numero = arrayColores[2].trim();
                        din = arrayColores[5].trim();
                        boolean existeCuneta = odb.ConsultarCuenta(numero);

                        if (existeCuneta) {
                            int IdUsuario = odb.VerificarUsuario(propNom, propApe);
                            int IdCuenta = odb.CrearCuenta(IdUsuario, numero);
                            odb.CrearDinero(IdCuenta, din);

                            msg = "Creacion exitosa";
                        } else {
                            msg = "cuenta ya creada";
                        }
                        return msg;
                    case "borrar":
                        numero = arrayColores[2].trim();
                        return numero;
                    case "modificar":
                        numero = arrayColores[2].trim();
                        propNom = arrayColores[3].trim();
                        propApe = arrayColores[4].trim();

                        int id = odb.GetIdUsuarioCuentas(numero);
                        int IdUsuario = odb.VerificarUsuario(propNom, propApe);

                        if (!(id == IdUsuario)) {
                            odb.CambiarUsuarioCuenta(IdUsuario, numero);
                        }

                        return numero + " " + propNom + " " + propApe;
                    default:
                        return "Error";
                }
            case "2":
                String numeroc;
                String dinero;
                String msgs;
                switch (arrayColores[1]) {
                    case "consignacion":
                        numeroc = arrayColores[2];
                        dinero = arrayColores[3];
                        odb.Transacion(numeroc, 1, Integer.parseInt(dinero));
                        return "consignacion";
                    case "retiro":
                        numeroc = arrayColores[2];
                        dinero = arrayColores[3];
                        odb.Transacion(numeroc, 2, Integer.parseInt(dinero));
                        return "retiro";
                    default:
                        return "Error";
                }

            case "3":
                return "Danilo3";
            default:
                return "Error";
        }

    }
}
