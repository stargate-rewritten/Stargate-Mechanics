package org.sgrewritten.stargatemechanics.utils;

import jakarta.json.JsonObject;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.sgrewritten.stargatemechanics.StargateMechanics;
import org.sgrewritten.stargatemechanics.exception.ParseException;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;

class TimeParserTest {

    @ParameterizedTest
    @JsonFileSource(resources = "/temporalExpressions.json")
    void parseTime(JsonObject object) throws ParseException {
        long time = TimeParser.parseTime(object.getString("expression"));
        Assertions.assertEquals( object.getJsonNumber("value").longValue(),time);
    }

    @ParameterizedTest
    @JsonFileSource(resources = "/invalidTemporalExpressions.json")
    void parseTime_Invalid(JsonObject object) {
        Assertions.assertThrows( ParseException.class, () -> TimeParser.parseTime(object.getString("expression")));
    }
}