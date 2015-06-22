package com.paulhammant.greyangular;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;

public class JacksonTest {

    public static class Location {
        @JsonProperty("Text")
        public String text;
        @JsonProperty("Value")
        public String value;
    }

    @Test(groups = "unit")
    public void testtt() throws IOException {

        Location[] locations = new Location[2];
        locations[0] = new Location();
        locations[0].text = "one";
        locations[0].value = "1|one";
        locations[1] = new Location();
        locations[1].text = "two";
        locations[1].value = "2|two";

        String s = new ObjectMapper().writeValueAsString(locations);

        Location[] hits2 = new ObjectMapper().readValue(s, Location[].class);

    }

}
