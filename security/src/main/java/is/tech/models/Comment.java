package is.tech.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Date;

@Data
@ToString
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String author;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "task_id")
    private Long taskId;
}
