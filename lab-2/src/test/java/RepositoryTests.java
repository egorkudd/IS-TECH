import is.technologies.enums.TaskType;
import is.technologies.models.Employee;
import is.technologies.models.Task;
import is.technologies.repositories.CRUDRepository;
import is.technologies.repositories.ChildEntityRepository;
import is.technologies.repositories.mybatis.implementations.EmployeeMyBatisRepositoryImpl;
import is.technologies.repositories.mybatis.implementations.TaskMyBatisRepositoryImpl;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RepositoryTests {
    private static CRUDRepository<Employee> employeeRepository;
    private static ChildEntityRepository<Task> taskRepository;

    public RepositoryTests() throws IOException {
//        try {
//            employeeRepository = new EmployeeJDBCRepository(
//                    "jdbc:mysql://localhost:3306/itmo_lab2",
//                    "root",
//                    "testtest"
//            );
//            taskRepository = new TaskJDBCRepository(
//                    "jdbc:mysql://localhost:3306/itmo_lab2",
//                    "root",
//                    "testtest"
//            );
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

//        employeeRepository = new EmployeeRepository();
//        taskRepository = new TaskRepository();

        employeeRepository = new EmployeeMyBatisRepositoryImpl();
        taskRepository = new TaskMyBatisRepositoryImpl();
    }

    @AfterAll
    public static void clearAll() throws SQLException {
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    public void taskTest() {
        try {
            Employee employee1 = new Employee();
            employee1.setId(193);
            employee1.setName("Dima");
            employee1.setBirthday(LocalDate.of(2003, 6, 7));

            Employee savedEmployee1 = employeeRepository.save(employee1);

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

            Assertions.assertEquals(savedTask1, taskRepository.getById(savedTask1.getId()));
            Assertions.assertEquals(savedTask2, taskRepository.getById(savedTask2.getId()));

            List<Task> tasks = taskRepository.getAll();
            Assertions.assertTrue(tasks.contains(savedTask1));
            Assertions.assertTrue(tasks.contains(savedTask2));

            List<Task> tasksOfEmployee = taskRepository.getAllByParentId(savedEmployee1.getId());
            Assertions.assertTrue(tasksOfEmployee.contains(savedTask1));
            Assertions.assertTrue(tasksOfEmployee.contains(savedTask2));

            savedTask1.setName("Go for a walk");
            savedTask1.setDeadLine(LocalDate.now());
            savedTask1.setDescription("You need to breath");
            savedTask1.setType(TaskType.UNIMPORTANT);
            Task updatedTask1 = taskRepository.update(savedTask1);
            Assertions.assertEquals(updatedTask1, taskRepository.getById(savedTask1.getId()));

            taskRepository.deleteById(updatedTask1.getId());
            Task notExistingTask1 = taskRepository.getById(updatedTask1.getId());
            Assertions.assertNull(notExistingTask1);

            taskRepository.deleteByEntity(savedTask2);
            Task notExistingTask2 = taskRepository.getById(savedTask2.getId());
            Assertions.assertNull(notExistingTask2);

            Task savedTask3 = taskRepository.save(savedTask1);
            Assertions.assertEquals(savedTask3, taskRepository.getById(savedTask3.getId()));

            Task savedTask4 = taskRepository.save(savedTask2);
            Assertions.assertEquals(savedTask4, taskRepository.getById(savedTask4.getId()));

            taskRepository.deleteAll();
            Task notExistingTask3 = taskRepository.getById(savedTask3.getId());
            Task notExistingTask4 = taskRepository.getById(savedTask4.getId());
            Assertions.assertNull(notExistingTask3);
            Assertions.assertNull(notExistingTask4);

            employeeRepository.deleteByEntity(savedEmployee1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void employeeTest() {
        try {
            Employee employee1 = new Employee();
            employee1.setId(19);
            employee1.setName("Ivan");
            employee1.setBirthday(LocalDate.of(2003, 1, 1));

            Employee employee2 = new Employee();
            employee2.setId(7);
            employee2.setName("Oleg");
            employee2.setBirthday(LocalDate.of(2003, 5, 17));

            Employee savedEmployee1 = employeeRepository.save(employee1);
            Employee savedEmployee2 = employeeRepository.save(employee2);

            Assertions.assertEquals(savedEmployee1, employeeRepository.getById(savedEmployee1.getId()));
            Assertions.assertEquals(savedEmployee2, employeeRepository.getById(savedEmployee2.getId()));

            List<Employee> employees = employeeRepository.getAll();
            Assertions.assertTrue(employees.contains(savedEmployee1));
            Assertions.assertTrue(employees.contains(savedEmployee2));

            savedEmployee1.setName("Alex");
            savedEmployee1.setBirthday(LocalDate.of(2004, 4, 5));
            Employee updatedEmployee1 = employeeRepository.update(savedEmployee1);
            Assertions.assertEquals(updatedEmployee1, employeeRepository.getById(savedEmployee1.getId()));

            employeeRepository.deleteById(updatedEmployee1.getId());
            Employee notExistingEmployee1 = employeeRepository.getById(updatedEmployee1.getId());
            Assertions.assertNull(notExistingEmployee1);

            employeeRepository.deleteByEntity(savedEmployee2);
            Employee notExistingEmployee2 = employeeRepository.getById(savedEmployee2.getId());
            Assertions.assertNull(notExistingEmployee2);

            Employee savedEmployee3 = employeeRepository.save(savedEmployee1);
            Assertions.assertEquals(savedEmployee3, employeeRepository.getById(savedEmployee3.getId()));

            Employee savedEmployee4 = employeeRepository.save(savedEmployee2);
            Assertions.assertEquals(savedEmployee4, employeeRepository.getById(savedEmployee4.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
