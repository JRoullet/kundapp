package jroullet.mswebapp.clients;

import jroullet.mswebapp.dto.EmailDto;
import jroullet.mswebapp.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="ms-identity")
public interface IdentityFeignClient {

//    // PATIENT
//        @GetMapping(value = "/patient/all", consumes = "application/json")
//        List<Patient> findAll();
//
//        //Find
//        @GetMapping(value = "/patient/get/{id}", consumes = "application/json")
//        Patient getPatientById(@PathVariable Long id);
//
//        //Create
//        @PostMapping(value = "/patient/new", consumes = "application/json")
//        Patient createPatient(@RequestBody Patient patient);
//
//        //Update
//        @PutMapping(value = "/patient/update", consumes = "application/json")
//        Patient updatePatientById(@RequestBody Patient updatedPatient);

    // USER
        @PostMapping(value= "/api/user", consumes = "application/json")
        User findUserByEmail(@RequestBody EmailDto emailDto);

        @PostMapping(value = "/user/create", consumes = "application/json")
        User createUser(@RequestBody User user);

        @PostMapping(value = "/user/create-admin", consumes = "application/json")
        User createAdmin(@RequestBody User user);
}
