package com.paulhammant.greyangular.moco;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class SearchContentHandler extends GreyAbstractContentResponseHandler {

    @Override
    protected MessageContent responseContent(Request request) {
        return MessageContent.content(getParam(request, "callback") + " (" + getContent(getParam(request, "from"), getParam(request, "to"), getParam(request, "when")) + ")");
    }

    protected abstract String getContent(String from, String to, String when);

}
