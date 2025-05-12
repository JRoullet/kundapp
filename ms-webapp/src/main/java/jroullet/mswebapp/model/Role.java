package jroullet.mswebapp.model;

public enum Role {
    CLIENT,TEACHER,ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
