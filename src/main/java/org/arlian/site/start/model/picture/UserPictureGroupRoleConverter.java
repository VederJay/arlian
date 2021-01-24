package org.arlian.site.start.model.picture;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class UserPictureGroupRoleConverter implements AttributeConverter<UserPictureGroupRole, String> {

    @Override
    public String convertToDatabaseColumn(UserPictureGroupRole userPictureGroupRole) {
        if (userPictureGroupRole == null) {
            return null;
        }
        return userPictureGroupRole.getCode();
    }

    @Override
    public UserPictureGroupRole convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(UserPictureGroupRole.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
