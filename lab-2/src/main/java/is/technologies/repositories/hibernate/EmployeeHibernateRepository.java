package is.technologies.repositories.hibernate;

import is.technologies.models.Employee;

/**
 * Employee's repository with hibernate framework
 */
public class EmployeeHibernateRepository extends HibernateRepository<Employee> {
    public EmployeeHibernateRepository() {
        tableName = "employees";
        aClass = Employee.class;
        getSessionFactory();
    }
}
