package is.technologies.models;

import lombok.*;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.*;

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

    public void setBirthday(Date birthday) {
        this.birthday = birthday.toLocalDate();
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
