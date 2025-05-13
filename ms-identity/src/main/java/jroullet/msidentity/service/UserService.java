package jroullet.msidentity.service;

import jroullet.msidentity.model.User;

public interface UserService {
    User findUserByEmail(String email);
}
