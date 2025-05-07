package jroullet.mswebapp.model;

public enum Role {
    CLIENT,TEACHER,ADMIN;

    public String toAuthority() {
        return "ROLE_" + this.name();
    }

    public static Role fromAuthority(String authority) {
        return Role.valueOf(authority.replace("ROLE_", ""));
    }
}
