/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author xcojcama
 */
@Entity
@Table(name = "dinero")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dinero.findAll", query = "SELECT d FROM Dinero d")
    , @NamedQuery(name = "Dinero.findById", query = "SELECT d FROM Dinero d WHERE d.id = :id")
    , @NamedQuery(name = "Dinero.findByValorinicial", query = "SELECT d FROM Dinero d WHERE d.valorinicial = :valorinicial")
    , @NamedQuery(name = "Dinero.findByValoractual", query = "SELECT d FROM Dinero d WHERE d.valoractual = :valoractual")})
public class Dinero implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Valor_inicial")
    private int valorinicial;
    @Basic(optional = false)
    @Column(name = "Valor_actual")
    private int valoractual;
    @JoinColumn(name = "ID_Cuenta", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Cuenta iDCuenta;

    public Dinero() {
    }

    public Dinero(Integer id) {
        this.id = id;
    }

    public Dinero(Integer id, int valorinicial, int valoractual) {
        this.id = id;
        this.valorinicial = valorinicial;
        this.valoractual = valoractual;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getValorinicial() {
        return valorinicial;
    }

    public void setValorinicial(int valorinicial) {
        this.valorinicial = valorinicial;
    }

    public int getValoractual() {
        return valoractual;
    }

    public void setValoractual(int valoractual) {
        this.valoractual = valoractual;
    }

    public Cuenta getIDCuenta() {
        return iDCuenta;
    }

    public void setIDCuenta(Cuenta iDCuenta) {
        this.iDCuenta = iDCuenta;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dinero)) {
            return false;
        }
        Dinero other = (Dinero) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Dinero[ id=" + id + " ]";
    }
    
}
