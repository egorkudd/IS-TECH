package is.technologies;

import com.mysql.cj.log.LogFactory;
import is.technologies.models.Employee;
import is.technologies.repositories.hibernate.EmployeeHibernateRepository;
import is.technologies.repositories.hibernate.HibernateRepository;
import is.technologies.repositories.jdbc.EmployeeJDBCRepository;
import is.technologies.repositories.jdbc.JDBCRepository;
import is.technologies.repositories.mybatis.implementations.EmployeeMyBatisRepositoryImpl;
import is.technologies.repositories.mybatis.implementations.MyBatisRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {;
        List<Employee> employees = new ArrayList<>();

        String defaultName = "Ivan";
        LocalDate defaultDate = LocalDate.of(2000, 1, 1);

        IntStream.range(0, 100).forEach(
                i -> {
                    Employee employee = new Employee();
                    employee.setName(defaultName.concat(String.valueOf(i)));
                    employee.setBirthday(defaultDate.plusDays(i));
                    employees.add(employee);
                }
        );

        System.out.println("---------------------");
        testJDBCTime(employees);
        System.out.println("---------------------");
        testHibernateTime(employees);
        System.out.println("---------------------");
        testMyBatisTime(employees);
        System.out.println("---------------------");
    }

    public static void testJDBCTime(List<Employee> employees) {
        JDBCRepository<Employee> employeeRepository = new EmployeeJDBCRepository(
                "jdbc:mysql://localhost:3306/itmo_lab2",
                "root",
                "testtest"
        );

        long startTime = System.nanoTime();
        employees.stream().forEach(employee -> {
            try {
                employeeRepository.save(employee);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Time to insert: "
                .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                .concat(" ms")
        );

        startTime = System.nanoTime();
        try {
            List<Employee> savedEmployees = employeeRepository.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Time to get all: "
                .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                .concat(" ms")
        );

        try {
            employeeRepository.deleteAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        employeeRepository.close();
    }

    public static void testHibernateTime(List<Employee> employees) {
        HibernateRepository<Employee> employeeRepository = new EmployeeHibernateRepository();

        long startTime = System.nanoTime();
        employees.stream().forEach(employeeRepository::save);

        System.out.println("Time to insert: "
                .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                .concat(" ms")
        );

        startTime = System.nanoTime();
        List<Employee> savedEmployees = employeeRepository.getAll();

        System.out.println("Time to get all: "
                .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                .concat(" ms")
        );

        employeeRepository.deleteAll();
        employeeRepository.close();
    }

    public static void testMyBatisTime(List<Employee> employees) {
        try {
            MyBatisRepository<Employee> employeeRepository = new EmployeeMyBatisRepositoryImpl();

            long startTime = System.nanoTime();
            employees.stream().forEach(employeeRepository::save);

            System.out.println("Time to insert: "
                    .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                    .concat(" ms")
            );

            startTime = System.nanoTime();
            List<Employee> savedEmployees = employeeRepository.getAll();

            System.out.println("Time to get all: "
                    .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                    .concat(" ms")
            );

            employeeRepository.deleteAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}