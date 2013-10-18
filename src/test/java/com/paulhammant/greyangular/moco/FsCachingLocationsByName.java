package com.paulhammant.greyangular.moco;

import com.paulhammant.greyangular.LocationMap;
import com.paulhammant.greyangular.LocationsByName;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FsCachingLocationsByName implements LocationsByName {

    private String cacheDir = FsCachingLocationsByName.class
            .getProtectionDomain().getCodeSource()
            .getLocation().getPath()
            .replace("/target/test-classes/", "/src/test/caches/location/");

    private LocationsByName delegate;
    private final String type;

    public FsCachingLocationsByName(LocationsByName delegate, String type) {
        this.delegate = delegate;
        this.type = type;
    }

    public String getGreyhoundLocations(String term, Object ghSessionId, LocationMap locationMap) {
        File file = new File(cacheDir + type + "_" + term);
        try {
            return tryInCacheFirst(file);
        } catch (IOException e) {
            try {
                return delegateThenCache(term, ghSessionId, locationMap, file);
            } catch (IOException e1) {
                throw new RuntimeException(e);
            }
        }
    }

    private String tryInCacheFirst(File file) throws IOException {
        return FileUtils.readFileToString(file);
    }

    private String delegateThenCache(String term, Object ghSessionId, LocationMap locationMap, File file) throws IOException {
        String result = delegate.getGreyhoundLocations(term, ghSessionId, locationMap);
        FileUtils.writeStringToFile(file, result);
        return result;
    }
}
