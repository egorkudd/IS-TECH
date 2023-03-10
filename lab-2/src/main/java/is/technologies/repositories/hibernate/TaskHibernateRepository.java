package is.technologies.repositories.hibernate;

import is.technologies.models.Task;
import is.technologies.repositories.ChildEntityRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.List;

public class TaskHibernateRepository
        extends HibernateRepository<Task> implements ChildEntityRepository<Task> {
    public TaskHibernateRepository() {
        tableName = "tasks";
        aClass = Task.class;
        getSessionFactory();
    }

    @Override
    public List<Task> getAllByParentId(long id){
        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Task> criteria = builder.createQuery(aClass);
            Root<Task> root = criteria.from(aClass);
            criteria.select(root).where(builder.equal(root.get("employeeId"), id));
            return session.createQuery(criteria).setMaxResults(5).getResultList();
        }
    }
}
