package is.technologies.models;

import is.technologies.enums.TaskType;
import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.SelectBeforeUpdate;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

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

    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine.toLocalDate();
    }
}
