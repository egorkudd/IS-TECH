package org.example.controllers;

import org.example.models.Comment;
import org.example.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/comments")
    public ResponseEntity<Long> add(@RequestBody Comment comment) {
        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment.getId());
    }

    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(commentRepository.findAll());
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getById(@PathVariable Long id) {
        Optional<Comment> commentOpt = commentRepository.findById(id);
        return commentOpt
                .map(comment -> ResponseEntity.status(HttpStatus.OK).body(comment))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody Comment comment) {
        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Comment commentToUpdate = commentOpt.get();

        if (comment.getDescription() != null){
            commentToUpdate.setDescription(comment.getDescription());
        }

        if (comment.getAuthor() != null){
            commentToUpdate.setAuthor(comment.getAuthor());
        }

        commentRepository.save(commentToUpdate);

        return ResponseEntity.status(HttpStatus.OK).body(commentToUpdate.getId());
    }

    @DeleteMapping("/comments/{id}")
    private void delete(@PathVariable Long id) {
        commentRepository.deleteById(id);
    }

    @GetMapping("/comments/taskId")
    public ResponseEntity<List<Comment>> getAllByTaskId(@RequestParam Long taskId) {
        List<Comment> tasks = commentRepository.getAllByTaskId(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }
}
