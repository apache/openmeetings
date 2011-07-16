package org.openmeetings.test.dao.base;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityFactoryUtils {

	private static final String PERSISTENCE_UNIT = "openmeetings";
	private static EntityManagerFactory entityManagerFactory;
    public static final ThreadLocal<EntityManager> entityManager = new ThreadLocal<EntityManager>();

    private static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            // Create the EntityManagerFactory
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return entityManagerFactory;
    }

    /**
     * Gets EntityManager instance for current thread
     * @return entityManager
     */
    public static EntityManager getEntityManager() {
        EntityManager em = entityManager.get();

        // Create a new EntityManager
        if (em == null) {
            em = getEntityManagerFactory().createEntityManager();
            entityManager.set(em);
        }
        return em;
    }

    /**
     * Close EntityManager instance
     */
    public static void closeEntityManager() {
        EntityManager em = entityManager.get();
        entityManager.set(null);
        if (em != null) {
            em.close();
        }
    }
}
