package org.arlian.site.start.model.picture;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class OrientationConverter implements AttributeConverter<Orientation, String> {

    @Override
    public String convertToDatabaseColumn(Orientation orientation) {
        if (orientation == null) {
            return null;
        }
        return orientation.getCode();
    }

    @Override
    public Orientation convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(Orientation.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
