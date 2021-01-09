package org.arlian.site.start.model.picture;

public enum Orientation {
    HORIZONTAL("H"), VERTICAL("V");

    private String code;

    private Orientation(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
