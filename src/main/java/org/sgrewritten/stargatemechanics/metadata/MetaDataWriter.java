package org.sgrewritten.stargatemechanics.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sgrewritten.stargatemechanics.exception.ParseException;

public class MetaDataWriter {

    public static String addMetaData(@NotNull MetaData key, @NotNull String value, @Nullable String previousData) throws ParseException {
        JsonElement element;
        if(previousData == null || previousData.isBlank()) {
            element = new JsonObject();
        } else {
            element = JsonParser.parseString(previousData);
        }
        if(element instanceof JsonObject object){
            object.add(key.name().toLowerCase(), new JsonPrimitive(value));
            return object.toString();
        }
        throw new ParseException("Invalid metadata '" + previousData + "', data is not json map");
    }

    public static String removeMetaData(@NotNull MetaData key, @Nullable String previousData) throws ParseException {
        JsonElement element;
        if(previousData == null || previousData.isBlank()) {
            return "";
        } else {
            element = JsonParser.parseString(previousData);
        }
        if(element instanceof JsonObject object){
            object.remove(key.name().toLowerCase());
            return object.toString();
        }
        throw new ParseException("Invalid metadata '" + previousData + "', data is not json map");
    }
}
