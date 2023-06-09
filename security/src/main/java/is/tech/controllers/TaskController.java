package is.tech.controllers;

import is.tech.models.Task;
import is.tech.repositories.TaskRepository;
import is.tech.security.user.Role;
import is.tech.security.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<Long> add(@RequestBody Task task, @AuthenticationPrincipal User user) {
        task.setAuthor(user.getEmail());
        Task newTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask.getId());
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAll(@AuthenticationPrincipal User user) {
        List<Task> tasks = taskRepository.findAll().stream()
                .filter(task -> task.getEmployeeId().equals(user.getEmployeeId()))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null || !task.getEmployeeId().equals(user.getEmployeeId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Long> update(
            @PathVariable Long id,
            @RequestBody Task updatedTask,
            @AuthenticationPrincipal User user
    ) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty() || taskOpt.get().getEmployeeId().equals(user.getEmployeeId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Task task = taskOpt.get();

        if (updatedTask.getType() != null) {
            task.setType(updatedTask.getType());
        }

        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(task.getId());
    }

    @DeleteMapping("/{id}")
    private void delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null &&
                (task.getAuthor().equals(user.getEmail()) || user.getRole().equals(Role.ADMIN))
        ) {
            taskRepository.deleteById(id);
        }
    }

    @GetMapping("/employeeId")
    public ResponseEntity<List<Task>> getAllByEmployeeId(@RequestParam Long employeeId) {
        List<Task> tasks = taskRepository.getAllByEmployeeId(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }
}