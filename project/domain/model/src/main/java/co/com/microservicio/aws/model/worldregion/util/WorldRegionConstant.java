package co.com.microservicio.aws.model.worldregion.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorldRegionConstant {
    public static final String PARAM_PLACE_TYPE = "placeType";
    public static final String PARAM_PLACE = "place";
    public static final String PARAM_CODE = "code";
    public static final String SEPARATOR_CODE = "-";
    public static final String MSG_LIST_SUCCESS = "Listed successfull!";
    public static final String MSG_SAVED_SUCCESS = "Saved successfull!";
    public static final String MSG_UPDATED_SUCCESS = "Updated successfull!";
    public static final String MSG_DELETED_SUCCESS = "Deleted successfull!";
}