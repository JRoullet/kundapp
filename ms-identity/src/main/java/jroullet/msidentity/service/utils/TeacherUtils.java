package jroullet.msidentity.service.utils;
import jroullet.msidentity.dto.TeacherUpdateDTO;
import jroullet.msidentity.model.Address;
import jroullet.msidentity.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TeacherUtils {

    public void updateTeacherFields(User teacher, TeacherUpdateDTO dto) {

        if (dto.getFirstName() != null && !dto.getFirstName().trim().isEmpty()) {
            teacher.setFirstName(dto.getFirstName().trim());
        }

        if (dto.getLastName() != null && !dto.getLastName().trim().isEmpty()) {
            teacher.setLastName(dto.getLastName().trim());
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            teacher.setEmail(dto.getEmail().trim().toLowerCase());
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
            teacher.setPhoneNumber(dto.getPhoneNumber().trim().isEmpty() ? null : dto.getPhoneNumber().trim());
        }

        if (dto.getDateOfBirth() != null) {
            teacher.setDateOfBirth(dto.getDateOfBirth());
        }

        if (dto.getBiography() != null) {
            teacher.setBiography(dto.getBiography().trim().isEmpty() ? null : dto.getBiography().trim());
        }

        if (dto.getAddress() != null) {
            if (teacher.getAddress() == null) {
                teacher.setAddress(new Address());
            }

            Address dtoAddress = dto.getAddress();
            Address teacherAddress = teacher.getAddress();

            if (dtoAddress.getStreet() != null && !dtoAddress.getStreet().trim().isEmpty()) {
                teacherAddress.setStreet(dtoAddress.getStreet().trim());
            }

            if (dtoAddress.getCity() != null && !dtoAddress.getCity().trim().isEmpty()) {
                teacherAddress.setCity(dtoAddress.getCity().trim());
            }

            if (dtoAddress.getZipCode() != null && !dtoAddress.getZipCode().trim().isEmpty()) {
                teacherAddress.setZipCode(dtoAddress.getZipCode().trim());
            }

            if (dtoAddress.getCountry() != null && !dtoAddress.getCountry().trim().isEmpty()) {
                teacherAddress.setCountry(dtoAddress.getCountry().trim());
            }
        }
    }
}
