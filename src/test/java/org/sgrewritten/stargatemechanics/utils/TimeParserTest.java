package org.sgrewritten.stargatemechanics.utils;

import jakarta.json.JsonObject;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.*;

class TimeParserTest {

    @ParameterizedTest
    @JsonFileSource(resources = "/temporalExpressions.json")
    void parseTime(JsonObject object) {
        long time = TimeParser.parseTime(object.getString("expression"));
        Assertions.assertEquals(object.getInt("value"),time);
    }
}