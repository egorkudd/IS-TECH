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

public abstract class Repository<T extends Model> implements CRUDRepository<T> {
    protected SqlSessionFactory factory;
    protected Class repositoryClass;

    @Override
    public T save(T entity) {
        try (SqlSession session = factory.openSession()) {
            session.insert("save", entity);
            session.commit();
            return getById(entity.getId());
        }
    }

    @Override
    public void deleteById(long id) {
        try (SqlSession session = factory.openSession()) {
            session.delete("deleteById", id);
            session.commit();
        }
    }

    @Override
    public void deleteByEntity(T entity) {
        try (SqlSession session = factory.openSession()) {
            session.delete("deleteByEntity", entity);
            session.commit();
        }
    }

    @Override
    public void deleteAll() {
        try (SqlSession session = factory.openSession()) {
            session.delete("deleteAll");
            session.commit();
        }
    }

    @Override
    public T update(T entity) {
        try (SqlSession session = factory.openSession()) {
            session.update("update", entity);
            session.commit();
            return getById(entity.getId());
        }
    }

    @Override
    public T getById(long id) {
        try (SqlSession session = factory.openSession()) {
            return session.selectOne("getById", id);
        }
    }

    @Override
    public List<T> getAll() {
        try (SqlSession session = factory.openSession()) {
            return session.selectList("getAll");
        }
    }

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
