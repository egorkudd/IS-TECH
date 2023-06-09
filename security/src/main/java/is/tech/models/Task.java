package is.tech.models;

import is.tech.enums.TaskType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private TaskType type;

    @Column(name = "author")
    private String author;

    @Column(name = "employee_id")
    private Long employeeId;
}
