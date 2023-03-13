package is.technologies.repositories.hibernate;

import is.technologies.models.Model;
import is.technologies.repositories.CRUDRepository;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

/**
 * Abstract repository with hibernate framework
 */
public abstract class HibernateRepository<T extends Model> implements CRUDRepository<T> {
    private static SessionFactory sessionFactory;
    protected String tableName;
    protected Class<T> aClass;
    protected HibernateRepository() {
    }

    /**
     * Method to save model to database
     * @param entity is model to save
     * @return saved entity
     */
    @Override
    public T save(T entity) {
        entity.setId(0);
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();

            return getById(entity.getId());
            /* TODO : надо делать get ? Все ли поля обновляет persist или только id ?
                Вдруг у сущности будет поле с датой добавления (которая реализуется на уровне базы)
                Обновит ли её тогда persist ?
             */

        }
    }

    /**
     * Method to delete model from database by id
     * @param id of model to delete
     */
    @Override
    public void deleteById(long id) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            T entity = session.get(aClass, id);
            if (entity != null) {
                session.remove(entity);
            }

            transaction.commit();
        }
    }

    /**
     * Method to delete model from database by model
     * @param entity is model to delete
     */
    @Override
    public void deleteByEntity(T entity) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            T entityToDelete = session.get(aClass, entity.getId());
            if (entityToDelete != null) {
                session.remove(entityToDelete);
            }

            transaction.commit();
        }
    }

    /**
     * Method to delete all models of one type
     */
    @Override
    public void deleteAll() {
        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder cBuilder = session.getCriteriaBuilder();
            CriteriaDelete<T> cq = cBuilder.createCriteriaDelete(aClass);
            cq.from(aClass);

            session.beginTransaction();
            session.createMutationQuery(cq).executeUpdate();
            session.getTransaction().commit();
        }
    }

    /**
     * Method for updating model in database
     * @param entity is model to update
     * @return updated entity
     */
    @Override
    public T update(T entity) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            T entityToUpdate = getById(entity.getId());
            if (entityToUpdate != null) {
                session.merge(entity);
            }

            session.getTransaction().commit();

        }

        return entity;
    }

    /**
     * Method to get model from database
     * @param id is id of model to get
     * @return model from database
     */
    @Override
    public T getById(long id) {
        try (Session session = getSessionFactory().openSession()) {
            return session.get(aClass, id);
        }
    }

    /**
     * Method to get all models of one type
     * @return list of models
     */
    @Override
    public List<T> getAll() {
        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteria = builder.createQuery(aClass);
            criteria.from(aClass);

            return session.createQuery(criteria).getResultList();
        }
    }

    /**
     * Method to get connection with database
     * @return session's factory
     */
    protected static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                        .configure("hibernate.cfg.xml").build();
                Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    /**
     * Method to close
     */
    public void close() {
        sessionFactory.close();
    }
}
