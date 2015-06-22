package com.paulhammant.greyangular.moco;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public abstract class ScheduleContentHandler extends GreyAbstractContentResponseHandler {

    @Override
    protected MessageContent responseContent(Request request) {
        return MessageContent.content(getParam(request, "callback") + " (" + getContent(getParam(request, "key")) + ")");
    }

    protected abstract String getContent(String key);

}
