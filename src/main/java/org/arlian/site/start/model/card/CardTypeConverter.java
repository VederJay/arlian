package org.arlian.site.start.model.card;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CardTypeConverter implements AttributeConverter<CardType, Integer> {


    @Override
    public Integer convertToDatabaseColumn(CardType cardType){
        if(cardType == null)
            return null;

        return cardType.getValue();
    }

    @Override
    public CardType convertToEntityAttribute(Integer dbData){
        if(dbData == null)
            return null;

        return CardType.valueOf(dbData);
    }
}
