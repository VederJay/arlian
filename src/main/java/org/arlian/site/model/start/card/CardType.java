package org.arlian.site.model.start.card;

import java.util.HashMap;
import java.util.Map;

public enum CardType {

    /*
     * VALUES
     */

    TEXT_LINKS(0),
    IMAGE_LINKS(100);


    /*
     * ATTRIBUTES
     */

    private final int value;
    private static Map<Integer, CardType> map = new HashMap<>();


    /*
     * CONSTRUCTOR
     */

    CardType(int value){
        this.value = value;
    }


    /*
     * GETTERS
     */

    public int getValue(){
        return value;
    }

    public String getName(){
        return name();
    }


    /*
     * CONVERTERS - methods used by converter class (for database storage)
     */

    static{
        for(CardType cardType : CardType.values())
            map.put(cardType.value, cardType);
    }

    public static CardType valueOf(int statusTypeAsInt){
        return map.get(statusTypeAsInt);
    }
}
