package is.technologies.models;

import is.technologies.enums.TaskType;
import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.SelectBeforeUpdate;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Task data class
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task implements Serializable, Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(name = "dead_line")
    private LocalDate deadLine;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private TaskType type;

    @Column(name = "employee_id")
    private long employeeId;

    /**
     * Setter for deadline from business logic to this class
     * @param deadLine is deadline's date
     */
    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    /**
     * Setter for deadline from database to this class
     * @param deadLine is deadline's date
     */
    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine.toLocalDate();
    }
}
