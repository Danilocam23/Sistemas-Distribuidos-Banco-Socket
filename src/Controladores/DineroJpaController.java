/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Cuentas;
import Entidades.Dinero;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author xcojcama
 */
public class DineroJpaController implements Serializable {

    public DineroJpaController() {
        this.emf = Persistence.createEntityManagerFactory("Banco_SocketPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Dinero dinero) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cuentas IDCuentas = dinero.getIDCuentas();
            if (IDCuentas != null) {
                IDCuentas = em.getReference(IDCuentas.getClass(), IDCuentas.getIDCuentas());
                dinero.setIDCuentas(IDCuentas);
            }
            em.persist(dinero);
            if (IDCuentas != null) {
                IDCuentas.getDineroList().add(dinero);
                IDCuentas = em.merge(IDCuentas);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Dinero dinero) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dinero persistentDinero = em.find(Dinero.class, dinero.getIDDinero());
            Cuentas IDCuentasOld = persistentDinero.getIDCuentas();
            Cuentas IDCuentasNew = dinero.getIDCuentas();
            if (IDCuentasNew != null) {
                IDCuentasNew = em.getReference(IDCuentasNew.getClass(), IDCuentasNew.getIDCuentas());
                dinero.setIDCuentas(IDCuentasNew);
            }
            dinero = em.merge(dinero);
            if (IDCuentasOld != null && !IDCuentasOld.equals(IDCuentasNew)) {
                IDCuentasOld.getDineroList().remove(dinero);
                IDCuentasOld = em.merge(IDCuentasOld);
            }
            if (IDCuentasNew != null && !IDCuentasNew.equals(IDCuentasOld)) {
                IDCuentasNew.getDineroList().add(dinero);
                IDCuentasNew = em.merge(IDCuentasNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dinero.getIDDinero();
                if (findDinero(id) == null) {
                    throw new NonexistentEntityException("The dinero with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dinero dinero;
            try {
                dinero = em.getReference(Dinero.class, id);
                dinero.getIDDinero();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dinero with id " + id + " no longer exists.", enfe);
            }
            Cuentas IDCuentas = dinero.getIDCuentas();
            if (IDCuentas != null) {
                IDCuentas.getDineroList().remove(dinero);
                IDCuentas = em.merge(IDCuentas);
            }
            em.remove(dinero);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Dinero> findDineroEntities() {
        return findDineroEntities(true, -1, -1);
    }

    public List<Dinero> findDineroEntities(int maxResults, int firstResult) {
        return findDineroEntities(false, maxResults, firstResult);
    }

    private List<Dinero> findDineroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Dinero.class));
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

    public Dinero findDinero(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Dinero.class, id);
        } finally {
            em.close();
        }
    }

    public int getDineroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Dinero> rt = cq.from(Dinero.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
