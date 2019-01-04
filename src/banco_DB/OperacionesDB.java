/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banco_DB;

import Controladores.CuentasJpaController;
import Controladores.DineroJpaController;
import Controladores.UsuariosJpaController;
import Entidades.Cuentas;
import Entidades.Dinero;
import Entidades.Usuarios;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author xcojcama
 */
public class OperacionesDB {
    
    
    public int CrearUsuario(String nom, String Ape){
        UsuariosJpaController CUsuarios = new UsuariosJpaController();
        Usuarios u = new Usuarios();
        u.setNombres(nom);
        u.setApellidos(Ape);
        return CUsuarios.create(u);
    }
    
    public int CrearCuenta (int idUsuario, String numerocuenta){
        CuentasJpaController CCuenta = new CuentasJpaController();        
        Cuentas c = new Cuentas();
        Usuarios u = new Usuarios();
        java.util.Date fecha = new Date();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String reportDate = df.format(fecha);        
        u.setIDUsuarios(idUsuario);
        c.setIDUsuarios(u);
        c.setNumerocuenta(numerocuenta);
        c.setFechacreacion(reportDate);        
        return CCuenta.create(c);
    }
    
    public void CrearDinero(int IdCuenta, String Dinero){
        DineroJpaController CDinero = new DineroJpaController();
        Dinero d = new Dinero();
        Cuentas c = new Cuentas();
        
        c.setIDCuentas(IdCuenta);
        
        d.setIDCuentas(c);
        d.setValoractual(Integer.parseInt(Dinero));
        d.setValorinicial(Integer.parseInt(Dinero));
        
        CDinero.create(d);
    
    }
}