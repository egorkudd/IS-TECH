package org.example.controllers;

import org.example.models.Task;
import org.example.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/tasks")
    public ResponseEntity<Long> add(@RequestBody Task task) {
        Task newTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask.getId());
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(taskRepository.findAll());
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        return taskOpt
                .map(task -> ResponseEntity.status(HttpStatus.OK).body(task))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody Task updatedTask) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (!taskOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Task task = taskOpt.get();

        if (updatedTask.getName() != null){
            task.setName(updatedTask.getName());
        }

        if (updatedTask.getDeadline() != null){
            task.setDeadline(updatedTask.getDeadline());
        }

        if (updatedTask.getDescription() != null){
            task.setDescription(updatedTask.getDescription());
        }

        if (updatedTask.getType() != null){
            task.setType(updatedTask.getType());
        }

        if (updatedTask.getEmployeeId() == 0){
            task.setEmployeeId(updatedTask.getEmployeeId());
        }

        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(task.getId());
    }

    @DeleteMapping("/tasks/{id}")
    private void delete(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }

    @GetMapping("/tasks/employeeId")
    public ResponseEntity<List<Task>> getAllByEmployeeId(@RequestParam Long employeeId) {
        List<Task> tasks = taskRepository.getAllByEmployeeId(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }
}