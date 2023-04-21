package is.technologies;

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
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

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

        compareTime(employees);

    }

    private static void compareTime(List<Employee> employees) {
        saveToCash(employees);
        testJDBCTime(employees);
        testHibernateTime(employees);
        testMyBatisTime(employees);
    }

    private static void testJDBCTime(List<Employee> employees) {
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

        log.info("Time to insert: "
                .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                .concat(" ms")
        );

        startTime = System.nanoTime();
        try {
            List<Employee> savedEmployees = employeeRepository.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        log.info("Time to get all: "
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

    private static void testHibernateTime(List<Employee> employees) {
        HibernateRepository<Employee> employeeRepository = new EmployeeHibernateRepository();

        long startTime = System.nanoTime();
        employees.stream().forEach(employeeRepository::save);

        log.info("Time to insert: "
                .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                .concat(" ms")
        );

        startTime = System.nanoTime();
        List<Employee> savedEmployees = employeeRepository.getAll();

        log.info("Time to get all: "
                .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                .concat(" ms")
        );

        employeeRepository.deleteAll();
        employeeRepository.close();
    }

    private static void testMyBatisTime(List<Employee> employees) {
        try {
            MyBatisRepository<Employee> employeeRepository = new EmployeeMyBatisRepositoryImpl();

            long startTime = System.nanoTime();
            employees.stream().forEach(employeeRepository::save);

            log.info("Time to insert: "
                    .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                    .concat(" ms")
            );

            startTime = System.nanoTime();
            List<Employee> savedEmployees = employeeRepository.getAll();

            log.info("Time to get all: "
                    .concat(String.valueOf((System.nanoTime() - startTime) / 1_000_000))
                    .concat(" ms")
            );

            employeeRepository.deleteAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToCash(List<Employee> employees) {
        try {
            MyBatisRepository<Employee> employeeRepository = new EmployeeMyBatisRepositoryImpl();

            employees.stream().forEach(employeeRepository::save);

            List<Employee> savedEmployees = employeeRepository.getAll();

            employeeRepository.deleteAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}