package jroullet.msidentity.service.utils;
import jroullet.msidentity.dto.TeacherUpdateDTO;
import jroullet.msidentity.dto.UserUpdateDTO;
import jroullet.msidentity.model.Address;
import jroullet.msidentity.model.User;
import org.springframework.stereotype.Component;

@Component
public class UpdateUtils {

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
            Address userAddress = teacher.getAddress();

            if (dtoAddress.getStreet() != null && !dtoAddress.getStreet().trim().isEmpty()) {
                userAddress.setStreet(dtoAddress.getStreet().trim());
            }

            if (dtoAddress.getCity() != null && !dtoAddress.getCity().trim().isEmpty()) {
                userAddress.setCity(dtoAddress.getCity().trim());
            }

            if (dtoAddress.getZipCode() != null && !dtoAddress.getZipCode().trim().isEmpty()) {
                userAddress.setZipCode(dtoAddress.getZipCode().trim());
            }

            if (dtoAddress.getCountry() != null && !dtoAddress.getCountry().trim().isEmpty()) {
                userAddress.setCountry(dtoAddress.getCountry().trim());
            }
        }
    }

    public void updateUserFields(User user, UserUpdateDTO dto) {

        if (dto.getFirstName() != null && !dto.getFirstName().trim().isEmpty()) {
            user.setFirstName(dto.getFirstName().trim());
        }

        if (dto.getLastName() != null && !dto.getLastName().trim().isEmpty()) {
            user.setLastName(dto.getLastName().trim());
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail().trim().toLowerCase());
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty()) {
            user.setPhoneNumber(dto.getPhoneNumber().trim().isEmpty() ? null : dto.getPhoneNumber().trim());
        }

        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(dto.getDateOfBirth());
        }

        if (dto.getCredits() != null) {
            user.setCredits(dto.getCredits());
        }

        if (dto.getAddress() != null) {
            if (user.getAddress() == null) {
                user.setAddress(new Address());
            }

            Address dtoAddress = dto.getAddress();
            Address userAddress = user.getAddress();

            if (dtoAddress.getStreet() != null && !dtoAddress.getStreet().trim().isEmpty()) {
                userAddress.setStreet(dtoAddress.getStreet().trim());
            }

            if (dtoAddress.getCity() != null && !dtoAddress.getCity().trim().isEmpty()) {
                userAddress.setCity(dtoAddress.getCity().trim());
            }

            if (dtoAddress.getZipCode() != null && !dtoAddress.getZipCode().trim().isEmpty()) {
                userAddress.setZipCode(dtoAddress.getZipCode().trim());
            }

            if (dtoAddress.getCountry() != null && !dtoAddress.getCountry().trim().isEmpty()) {
                userAddress.setCountry(dtoAddress.getCountry().trim());
            }
        }
    }
}
