package org.arlian.site.start.model.picture;

public enum UserPictureGroupRole {
    OWNS("O"),
    INVITED_TO_SHARE("I"),
    SHARES("S");

    private String code;

    private UserPictureGroupRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
