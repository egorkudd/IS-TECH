package is.technologies.repositories.hibernate;

import is.technologies.models.Employee;

public class EmployeeRepository extends Repository<Employee> {
    public EmployeeRepository() {
        tableName = "employees";
        aClass = Employee.class;
    }
}
