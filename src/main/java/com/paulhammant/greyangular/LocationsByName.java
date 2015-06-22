package com.paulhammant.greyangular;

import java.io.IOException;

public interface LocationsByName {

    public String getGreyhoundLocations(String term, Object ghSessionId, LocationMap locationMap);
}
