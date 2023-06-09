package is.tech.controllers;

import is.tech.models.Comment;
import is.tech.repositories.CommentRepository;
import is.tech.security.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;

    @PostMapping
    public ResponseEntity<Long> add(
            @RequestBody Comment comment,
            @AuthenticationPrincipal User user
    ) {
        comment.setAuthor(user.getEmail());
        comment.setCreateDate(Date.valueOf(LocalDate.now()));
        System.out.println(1);
        Comment savedComment = commentRepository.save(comment);
        System.out.println(2);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getById(@PathVariable Long id) {
        Optional<Comment> commentOpt = commentRepository.findById(id);
        return commentOpt
                .map(comment -> ResponseEntity.status(HttpStatus.OK).body(comment))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PatchMapping("/{id}")
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

    @DeleteMapping("/{id}")
    private void delete(@PathVariable Long id) {
        commentRepository.deleteById(id);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllByTaskId(@RequestParam Long taskId) {
        List<Comment> tasks = commentRepository.getAllByTaskId(taskId);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }
}
