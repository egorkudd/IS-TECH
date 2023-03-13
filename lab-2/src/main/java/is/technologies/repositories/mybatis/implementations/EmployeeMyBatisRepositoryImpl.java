package is.technologies.repositories.mybatis.implementations;

import is.technologies.models.Employee;
import is.technologies.repositories.mybatis.interfaces.EmployeeMyBatisRepository;

import java.io.IOException;

/**
 * Employee's repository with MyBatis framework
 */
public class EmployeeMyBatisRepositoryImpl extends MyBatisRepository<Employee> {
    public EmployeeMyBatisRepositoryImpl() throws IOException {
        repositoryClass = EmployeeMyBatisRepository.class;
        factory = getFactory();
    }
}
