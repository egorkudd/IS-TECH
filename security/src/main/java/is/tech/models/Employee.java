package is.tech.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Date;

/**
 * Employee data class
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employees")
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Date birthday;
}
