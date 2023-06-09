package org.example.models;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Employee data class
 */
@Data
@ToString
@NoArgsConstructor
@Entity
@Table(name = "employees")
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Date birthday;
}
