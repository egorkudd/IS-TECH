package is.tech.controllers;

import is.tech.models.Employee;
import is.tech.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        return employeeOpt
                .map(employee -> ResponseEntity.status(HttpStatus.OK).body(employee))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Employee employee = employeeOpt.get();

        if (updatedEmployee.getName() != null){
            employee.setName(updatedEmployee.getName());
        }

        if (updatedEmployee.getBirthday() != null){
            employee.setBirthday(updatedEmployee.getBirthday());
        }

        employeeRepository.save(employee);

        return ResponseEntity.status(HttpStatus.OK).body(employee.getId());
    }

    @GetMapping("/name")
    public ResponseEntity<List<Employee>> getAllByName(@RequestParam String name) {
        List<Employee> employeeList = employeeRepository.getAllByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(employeeList);
    }
}
