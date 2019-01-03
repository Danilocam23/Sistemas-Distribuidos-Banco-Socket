/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.IllegalOrphanException;
import Controladores.exceptions.NonexistentEntityException;
import Entidades.Cuenta;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Usuarios;
import Entidades.Dinero;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author xcojcama
 */
public class CuentaJpaController implements Serializable {

    public CuentaJpaController() {
        this.emf = Persistence.createEntityManagerFactory("Banco_SocketPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cuenta cuenta) {
        if (cuenta.getDineroList() == null) {
            cuenta.setDineroList(new ArrayList<Dinero>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios IDUsuario = cuenta.getIDUsuario();
            if (IDUsuario != null) {
                IDUsuario = em.getReference(IDUsuario.getClass(), IDUsuario.getId());
                cuenta.setIDUsuario(IDUsuario);
            }
            List<Dinero> attachedDineroList = new ArrayList<Dinero>();
            for (Dinero dineroListDineroToAttach : cuenta.getDineroList()) {
                dineroListDineroToAttach = em.getReference(dineroListDineroToAttach.getClass(), dineroListDineroToAttach.getId());
                attachedDineroList.add(dineroListDineroToAttach);
            }
            cuenta.setDineroList(attachedDineroList);
            em.persist(cuenta);
            if (IDUsuario != null) {
                IDUsuario.getCuentaList().add(cuenta);
                IDUsuario = em.merge(IDUsuario);
            }
            for (Dinero dineroListDinero : cuenta.getDineroList()) {
                Cuenta oldIDCuentaOfDineroListDinero = dineroListDinero.getIDCuenta();
                dineroListDinero.setIDCuenta(cuenta);
                dineroListDinero = em.merge(dineroListDinero);
                if (oldIDCuentaOfDineroListDinero != null) {
                    oldIDCuentaOfDineroListDinero.getDineroList().remove(dineroListDinero);
                    oldIDCuentaOfDineroListDinero = em.merge(oldIDCuentaOfDineroListDinero);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cuenta cuenta) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cuenta persistentCuenta = em.find(Cuenta.class, cuenta.getId());
            Usuarios IDUsuarioOld = persistentCuenta.getIDUsuario();
            Usuarios IDUsuarioNew = cuenta.getIDUsuario();
            List<Dinero> dineroListOld = persistentCuenta.getDineroList();
            List<Dinero> dineroListNew = cuenta.getDineroList();
            List<String> illegalOrphanMessages = null;
            for (Dinero dineroListOldDinero : dineroListOld) {
                if (!dineroListNew.contains(dineroListOldDinero)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Dinero " + dineroListOldDinero + " since its IDCuenta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (IDUsuarioNew != null) {
                IDUsuarioNew = em.getReference(IDUsuarioNew.getClass(), IDUsuarioNew.getId());
                cuenta.setIDUsuario(IDUsuarioNew);
            }
            List<Dinero> attachedDineroListNew = new ArrayList<Dinero>();
            for (Dinero dineroListNewDineroToAttach : dineroListNew) {
                dineroListNewDineroToAttach = em.getReference(dineroListNewDineroToAttach.getClass(), dineroListNewDineroToAttach.getId());
                attachedDineroListNew.add(dineroListNewDineroToAttach);
            }
            dineroListNew = attachedDineroListNew;
            cuenta.setDineroList(dineroListNew);
            cuenta = em.merge(cuenta);
            if (IDUsuarioOld != null && !IDUsuarioOld.equals(IDUsuarioNew)) {
                IDUsuarioOld.getCuentaList().remove(cuenta);
                IDUsuarioOld = em.merge(IDUsuarioOld);
            }
            if (IDUsuarioNew != null && !IDUsuarioNew.equals(IDUsuarioOld)) {
                IDUsuarioNew.getCuentaList().add(cuenta);
                IDUsuarioNew = em.merge(IDUsuarioNew);
            }
            for (Dinero dineroListNewDinero : dineroListNew) {
                if (!dineroListOld.contains(dineroListNewDinero)) {
                    Cuenta oldIDCuentaOfDineroListNewDinero = dineroListNewDinero.getIDCuenta();
                    dineroListNewDinero.setIDCuenta(cuenta);
                    dineroListNewDinero = em.merge(dineroListNewDinero);
                    if (oldIDCuentaOfDineroListNewDinero != null && !oldIDCuentaOfDineroListNewDinero.equals(cuenta)) {
                        oldIDCuentaOfDineroListNewDinero.getDineroList().remove(dineroListNewDinero);
                        oldIDCuentaOfDineroListNewDinero = em.merge(oldIDCuentaOfDineroListNewDinero);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cuenta.getId();
                if (findCuenta(id) == null) {
                    throw new NonexistentEntityException("The cuenta with id " + id + " no longer exists.");
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
            Cuenta cuenta;
            try {
                cuenta = em.getReference(Cuenta.class, id);
                cuenta.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cuenta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Dinero> dineroListOrphanCheck = cuenta.getDineroList();
            for (Dinero dineroListOrphanCheckDinero : dineroListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cuenta (" + cuenta + ") cannot be destroyed since the Dinero " + dineroListOrphanCheckDinero + " in its dineroList field has a non-nullable IDCuenta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuarios IDUsuario = cuenta.getIDUsuario();
            if (IDUsuario != null) {
                IDUsuario.getCuentaList().remove(cuenta);
                IDUsuario = em.merge(IDUsuario);
            }
            em.remove(cuenta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cuenta> findCuentaEntities() {
        return findCuentaEntities(true, -1, -1);
    }

    public List<Cuenta> findCuentaEntities(int maxResults, int firstResult) {
        return findCuentaEntities(false, maxResults, firstResult);
    }

    private List<Cuenta> findCuentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cuenta.class));
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

    public Cuenta findCuenta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cuenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getCuentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cuenta> rt = cq.from(Cuenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
