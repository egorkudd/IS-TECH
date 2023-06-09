package org.example.models;

import lombok.*;
import org.example.enums.TaskType;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Task data class
 */
@Data
@ToString
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "deadline")
    private Date deadline;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private TaskType type;

    @Column(name = "author")
    private String author;

    @Column(name = "employee_id")
    private Long employeeId;
}
