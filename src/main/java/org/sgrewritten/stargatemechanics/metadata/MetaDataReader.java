package org.sgrewritten.stargatemechanics.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargatemechanics.exception.ParseException;

public class MetaDataReader {

    public static @Nullable String getData(@Nullable String data, MetaData dataType) throws ParseException {
        if(data == null || data.isBlank()){
            return null;
        }
        JsonElement element = JsonParser.parseString(data);
        if(element instanceof JsonObject object){
            JsonElement value = object.get(dataType.name().toLowerCase());
            if(value == null){
                throw new ParseException("Missing key '" + dataType.name().toLowerCase() + "'");
            }
            return value.getAsString();
        }
        throw new ParseException("Invalid metadata '" + data + "' is not a json map");
    }
}
