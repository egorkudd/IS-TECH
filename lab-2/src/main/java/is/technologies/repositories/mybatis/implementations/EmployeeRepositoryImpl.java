package is.technologies.repositories.mybatis.implementations;

import is.technologies.models.Employee;
import is.technologies.repositories.mybatis.interfaces.EmployeeRepository;

import java.io.IOException;

public class EmployeeRepositoryImpl extends Repository<Employee> {
    public EmployeeRepositoryImpl() throws IOException {
        repositoryClass = EmployeeRepository.class;
        factory = getFactory();
    }
}
