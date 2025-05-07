package jroullet.msidentity.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("TEACHER")
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherProfile extends User {

    @Column(length = 1000)
    private String biography;

}
