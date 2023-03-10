package is.technologies.repositories.mybatis.implementations;

import is.technologies.models.Task;
import is.technologies.repositories.ChildEntityRepository;
import is.technologies.repositories.mybatis.interfaces.TaskMyBatisRepository;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;

public class TaskMyBatisRepositoryImpl extends MyBatisRepository<Task> implements ChildEntityRepository<Task> {
    public TaskMyBatisRepositoryImpl() throws IOException {
        repositoryClass = TaskMyBatisRepository.class;
        factory = getFactory();
    }

    @Override
    public List<Task> getAllByParentId(long id) {
        try (SqlSession session = factory.openSession()) {
            return session.selectList("getAllByParentId", id);
        }
    }
}
