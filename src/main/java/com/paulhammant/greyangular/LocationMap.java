package com.paulhammant.greyangular;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
  This superficially looks like, but is not, a singleton.
 */
public class LocationMap {

    public static final Object lock = new Object();
    private static final String REQ_ATTR_KEY = "locationMap";

    private final Map<String, String> locationMap;

    public LocationMap(Map<String, String> locationMap) {
        this.locationMap = locationMap;
    }

    public static LocationMap getOrMakeMap(HttpServletRequest req) {
        Map<String, String> locationMap;
        synchronized (lock) {
            ServletContext servletContext = req.getSession().getServletContext();
            locationMap = (Map<String, String>) servletContext.getAttribute(REQ_ATTR_KEY);
            if (locationMap == null) {
                locationMap = new ConcurrentHashMap<String, String>();
                servletContext.setAttribute(REQ_ATTR_KEY, locationMap);
            }
        }
        return new LocationMap(locationMap);

    }

    public void consumeIfNeeded(String locationsString) throws IOException {
        Location[] locns = new ObjectMapper().readValue(locationsString, Location[].class);
        for (Location locn : locns) {
            if (!locationMap.containsKey(locn.text)) {
                locationMap.put(locn.text, locn.value);
            }
        }
    }

    public String valueFor(String from) {
        return locationMap.get(from);
    }

    public static class Location {
        @JsonProperty("Text")
        public String text;
        @JsonProperty("Value")
        public String value;
    }

}
