package com.paulhammant.greyangular.moco;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class ScheduleContentHandler extends GreyAbstractContentResponseHandler {

    @Override
    protected void writeContentResponse(FullHttpRequest request, ByteBuf buffer) {
        buffer.writeBytes((getParam(request, "callback") + " ").getBytes());
        String key = getParam(request, "key");
        String s = "(" + getContent(key) + ")";
        buffer.writeBytes(s.getBytes());
    }

    protected abstract String getContent(String key);

}
