package is.technologies.repositories.mybatis.implementations;

import is.technologies.models.Task;
import is.technologies.repositories.ChildEntityRepository;
import is.technologies.repositories.mybatis.interfaces.EmployeeRepository;
import is.technologies.repositories.mybatis.interfaces.TaskRepository;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class TaskRepositoryImpl extends Repository<Task> implements ChildEntityRepository<Task> {
    public TaskRepositoryImpl() throws IOException {
        repositoryClass = TaskRepository.class;
        factory = getFactory();
    }

    @Override
    public List<Task> getAllByParentId(long id) {
        try (SqlSession session = factory.openSession()) {
            return session.selectList("getAllByParentId", id);
        }
    }
}
