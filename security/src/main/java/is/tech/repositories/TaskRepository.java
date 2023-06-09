package is.tech.repositories;

import is.tech.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> getAllByEmployeeId(Long employeeId);
}
