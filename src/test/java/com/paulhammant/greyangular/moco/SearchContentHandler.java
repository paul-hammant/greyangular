package com.paulhammant.greyangular.moco;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class SearchContentHandler extends GreyAbstractContentResponseHandler {

    @Override
    protected void writeContentResponse(FullHttpRequest request, ByteBuf buffer) {
        buffer.writeBytes((getParam(request, "callback") + " ").getBytes());
        String from = getParam(request, "from");
        String to = getParam(request, "to");
        String when = getParam(request, "when");
        String s = "(" + getContent(from, to, when) + ")";
        buffer.writeBytes(s.getBytes());
    }

    protected abstract String getContent(String from, String to, String when);

}
