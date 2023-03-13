package is.technologies.models;

import lombok.*;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.*;

/**
 * Employee data class
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "employees")
public class Employee implements Serializable, Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private LocalDate birthday;

    /**
     * Setter for birthday from database to this class
     * @param birthday is employee's birthday
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday.toLocalDate();
    }

    /**
     * Setter for birthday from business logic to this class
     * @param birthday is employee's birthday
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
