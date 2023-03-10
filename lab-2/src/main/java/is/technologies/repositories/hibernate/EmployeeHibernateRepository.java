package is.technologies.repositories.hibernate;

import is.technologies.models.Employee;

public class EmployeeHibernateRepository extends HibernateRepository<Employee> {
    public EmployeeHibernateRepository() {
        tableName = "employees";
        aClass = Employee.class;
        getSessionFactory();
    }
}
