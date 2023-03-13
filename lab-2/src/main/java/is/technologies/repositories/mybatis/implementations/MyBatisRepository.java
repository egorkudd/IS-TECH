package is.technologies.repositories.mybatis.implementations;

import is.technologies.models.Model;
import is.technologies.repositories.CRUDRepository;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public abstract class MyBatisRepository<T extends Model> implements CRUDRepository<T> {
    protected SqlSessionFactory factory;
    protected Class repositoryClass;

    /**
     * Method to save model to database
     * @param entity is model to save
     * @return saved entity
     */
    @Override
    public T save(T entity) {
        try (SqlSession session = factory.openSession()) {
            session.insert("save", entity);
            session.commit();
            return getById(entity.getId());
        }
    }

    /**
     * Method to delete model from database by id
     * @param id of model to delete
     */
    @Override
    public void deleteById(long id) {
        try (SqlSession session = factory.openSession()) {
            session.delete("deleteById", id);
            session.commit();
        }
    }

    /**
     * Method to delete model from database by model
     * @param entity is model to delete
     */
    @Override
    public void deleteByEntity(T entity) {
        try (SqlSession session = factory.openSession()) {
            session.delete("deleteByEntity", entity);
            session.commit();
        }
    }

    /**
     * Method to delete all models of one type
     */
    @Override
    public void deleteAll() {
        try (SqlSession session = factory.openSession()) {
            session.delete("deleteAll");
            session.commit();
        }
    }

    /**
     * Method for updating model in database
     * @param entity is model to update
     * @return updated entity
     */
    @Override
    public T update(T entity) {
        try (SqlSession session = factory.openSession()) {
            session.update("update", entity);
            session.commit();
            return getById(entity.getId());
        }
    }

    /**
     * Method to get model from database
     * @param id is id of model to get
     * @return model from database
     */
    @Override
    public T getById(long id) {
        try (SqlSession session = factory.openSession()) {
            return session.selectOne("getById", id);
        }
    }

    /**
     * Method to get all models of one type
     * @return list of models
     */
    @Override
    public List<T> getAll() {
        try (SqlSession session = factory.openSession()) {
            return session.selectList("getAll");
        }
    }

    /**
     * Method to create and to get connection with database
     * @return Sql session's factory
     * @throws IOException
     */
    protected SqlSessionFactory getFactory() throws IOException {
        if (factory == null) {
            try (Reader reader = Resources.getResourceAsReader("mybatis.cfg.xml")) {
                factory = new SqlSessionFactoryBuilder().build(reader);
                factory.getConfiguration().addMapper(repositoryClass);
            }
        }

        return factory;
    }
}
