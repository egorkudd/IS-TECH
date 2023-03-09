package is.technologies;

import is.technologies.enums.TaskType;
import is.technologies.models.Employee;
import is.technologies.models.Task;
import is.technologies.repositories.CRUDRepository;
import is.technologies.repositories.ChildEntityRepository;
import is.technologies.repositories.mybatis.implementations.TaskRepositoryImpl;
import is.technologies.repositories.mybatis.interfaces.EmployeeRepository;
import is.technologies.repositories.mybatis.implementations.EmployeeRepositoryImpl;
import is.technologies.repositories.mybatis.interfaces.TaskRepository;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.time.LocalDate;


public class Main {
    private static SqlSessionFactory factory;

    public static void main(String[] args) throws SQLException, IOException {
        printMySQLVersion();
    }

    public static void printMySQLVersion() throws SQLException, IOException {
        String resource = "mybatis.cfg.xml";

        try (Reader reader = Resources.getResourceAsReader(resource)) {
            factory = new SqlSessionFactoryBuilder().build(reader);
            factory.getConfiguration().addMapper(TaskRepository.class);


            Employee employee1 = new Employee();
            employee1.setId(193);
            employee1.setName("Dima");
            employee1.setBirthday(LocalDate.of(2003, 6, 7));

            Employee savedEmployee1 = new EmployeeRepositoryImpl().save(employee1);
            System.out.println(savedEmployee1.getId());

            ChildEntityRepository<Task> taskRepository = new TaskRepositoryImpl();

            Task task1 = new Task();
            task1.setName("Sleep");
            task1.setDeadLine(LocalDate.of(2023, 3, 3));
            task1.setDescription("You need to sleep!!!");
            task1.setType(TaskType.IMPORTANT);
            task1.setEmployeeId(savedEmployee1.getId());

            Task task2 = new Task();
            task2.setName("Eat");
            task2.setDeadLine(LocalDate.of(2023, 3, 4));
            task2.setDescription("You should eat!!!");
            task2.setType(TaskType.MEDIUM);
            task2.setEmployeeId(savedEmployee1.getId());

            Task savedTask1 = taskRepository.save(task1);
            Task savedTask2 = taskRepository.save(task2);

            System.out.println(savedTask1);
            System.out.println(taskRepository.getById(savedTask1.getId()));
            System.out.println(savedTask2);
            System.out.println(taskRepository.getById(savedTask2.getId()));
        }
    }
}
