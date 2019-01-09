/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.IllegalOrphanException;
import Controladores.exceptions.NonexistentEntityException;
import Entidades.Cuentas;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Usuarios;
import Entidades.Dineros;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author Danilo Camacho
 */
public class CuentasJpaController implements Serializable {

    public CuentasJpaController() {
        this.emf = Persistence.createEntityManagerFactory("Banco_SocketPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public int create(Cuentas cuentas) {
        if (cuentas.getDinerosList() == null) {
            cuentas.setDinerosList(new ArrayList<Dineros>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios IDUsuarios = cuentas.getIDUsuarios();
            if (IDUsuarios != null) {
                IDUsuarios = em.getReference(IDUsuarios.getClass(), IDUsuarios.getIDUsuarios());
                cuentas.setIDUsuarios(IDUsuarios);
            }
            List<Dineros> attachedDineroList = new ArrayList<Dineros>();
            for (Dineros dineroListDineroToAttach : cuentas.getDinerosList()) {
                dineroListDineroToAttach = em.getReference(dineroListDineroToAttach.getClass(), dineroListDineroToAttach.getIDDinero());
                attachedDineroList.add(dineroListDineroToAttach);
            }
            cuentas.setDinerosList(attachedDineroList);
            em.persist(cuentas);
            em.flush();
            if (IDUsuarios != null) {
                IDUsuarios.getCuentasList().add(cuentas);
                IDUsuarios = em.merge(IDUsuarios);
            }
            for (Dineros dineroListDinero : cuentas.getDinerosList()) {
                Cuentas oldIDCuentasOfDineroListDinero = dineroListDinero.getIDCuentas();
                dineroListDinero.setIDCuentas(cuentas);
                dineroListDinero = em.merge(dineroListDinero);
                if (oldIDCuentasOfDineroListDinero != null) {
                    oldIDCuentasOfDineroListDinero.getDinerosList().remove(dineroListDinero);
                    oldIDCuentasOfDineroListDinero = em.merge(oldIDCuentasOfDineroListDinero);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return cuentas.getIDCuentas();
    }

    public void edit(Cuentas cuentas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cuentas persistentCuentas = em.find(Cuentas.class, cuentas.getIDCuentas());
            Usuarios IDUsuariosOld = persistentCuentas.getIDUsuarios();
            Usuarios IDUsuariosNew = cuentas.getIDUsuarios();

            if (IDUsuariosNew != null) {
                IDUsuariosNew = em.getReference(IDUsuariosNew.getClass(), IDUsuariosNew.getIDUsuarios());
                cuentas.setIDUsuarios(IDUsuariosNew);
            }

            cuentas = em.merge(cuentas);
            if (IDUsuariosOld != null && !IDUsuariosOld.equals(IDUsuariosNew)) {
                IDUsuariosOld.getCuentasList().remove(cuentas);
                IDUsuariosOld = em.merge(IDUsuariosOld);
            }
            if (IDUsuariosNew != null && !IDUsuariosNew.equals(IDUsuariosOld)) {
                IDUsuariosNew.getCuentasList().add(cuentas);
                IDUsuariosNew = em.merge(IDUsuariosNew);
            }

            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cuentas.getIDCuentas();
                if (findCuentas(id) == null) {
                    throw new NonexistentEntityException("The cuentas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cuentas cuentas;
            try {
                cuentas = em.getReference(Cuentas.class, id);
                cuentas.getIDCuentas();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cuentas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Dineros> dineroListOrphanCheck = cuentas.getDinerosList();
            for (Dineros dineroListOrphanCheckDinero : dineroListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cuentas (" + cuentas + ") cannot be destroyed since the Dinero " + dineroListOrphanCheckDinero + " in its dineroList field has a non-nullable IDCuentas field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuarios IDUsuarios = cuentas.getIDUsuarios();
            if (IDUsuarios != null) {
                IDUsuarios.getCuentasList().remove(cuentas);
                IDUsuarios = em.merge(IDUsuarios);
            }
            em.remove(cuentas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cuentas> findCuentasEntities() {
        return findCuentasEntities(true, -1, -1);
    }

    public List<Cuentas> findCuentasEntities(int maxResults, int firstResult) {
        return findCuentasEntities(false, maxResults, firstResult);
    }

    private List<Cuentas> findCuentasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cuentas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Cuentas findCuentas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cuentas.class, id);
        } finally {
            em.close();
        }
    }

    public boolean boolCuentas(String cuenta) {

        EntityManager em = getEntityManager();
        try {
            Query query = em.createNamedQuery("Cuentas.count");
            query.setParameter("numerocuenta", cuenta);
            long count = (long) query.getSingleResult();
            return ((count == 0) ? true : false);
        } finally {
            em.close();
        }
    }

    public int GetIdUsuarioCuentas(String cuenta) {

        EntityManager em = getEntityManager();

        Query query = em.createNamedQuery("Cuentas.findByNumerocuenta");
        query.setParameter("numerocuenta", cuenta);
        Cuentas c = (Cuentas) query.getSingleResult();
        int idUsuario = Integer.parseInt(c.getIDUsuarios().getIDUsuarios().toString());
        return idUsuario;
    }

    public int IdCuentas(String cuenta) {

        EntityManager em = getEntityManager();

        Query query = em.createNamedQuery("Cuentas.findByNumerocuenta");
        query.setParameter("numerocuenta", cuenta);
        Cuentas c = (Cuentas) query.getSingleResult();
        int idCuenta = c.getIDCuentas();
        return idCuenta;
    }

    public Cuentas GetCuenta(String cuenta) {

        EntityManager em = getEntityManager();

        Query query = em.createNamedQuery("Cuentas.findByNumerocuenta");
        query.setParameter("numerocuenta", cuenta);
        return (Cuentas) query.getSingleResult();

    }

    public int getCuentasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cuentas> rt = cq.from(Cuentas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
