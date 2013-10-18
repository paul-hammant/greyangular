package com.paulhammant.greyangular.moco;

import com.paulhammant.greyangular.LocationMap;
import com.paulhammant.greyangular.LocationsByName;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.IOException;

public class LocationContentHandler extends GreyAbstractContentResponseHandler {

    private LocationsByName locationsByName;
    private LocationMap locationMap;

    public LocationContentHandler(LocationsByName locationsByName, LocationMap locationMap) {
        this.locationsByName = locationsByName;
        this.locationMap = locationMap;
    }

    @Override
    protected void writeContentResponse(FullHttpRequest request, ByteBuf buffer) {
        buffer.writeBytes((getParam(request, "callback") + " ").getBytes());
        String term = null;
        try {
            term = locationsByName.getGreyhoundLocations(getParam(request, "term"), null, locationMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.writeBytes(term.getBytes());
    }

}
