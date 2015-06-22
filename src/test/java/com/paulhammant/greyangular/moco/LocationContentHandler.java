package com.paulhammant.greyangular.moco;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
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
    protected MessageContent responseContent(Request request) {
        return MessageContent.content(getParam(request, "callback") + " " + locationsByName.getGreyhoundLocations(getParam(request, "term"), null, locationMap));
    }

}
