package com.paulhammant.greyangular.moco;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AbstractContentResponseHandler;
import com.github.dreamhead.moco.model.MessageContent;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class PageResponse extends AbstractContentResponseHandler {

    private static final String pagePath = PageResponse.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("target/test-classes/", "src/main/webapp");
    private final String page;

    public PageResponse(String page) {
        this.page = page;
    }

    @Override
    protected String getContentType(HttpRequest request) {
        return "text/html";
    }

    @Override
    protected MessageContent responseContent(Request request) {
        File file = new File(pagePath, page + ".html");
        try {
            return MessageContent.content(FileUtils.readFileToString(file));
        } catch (IOException e) {
            return MessageContent.content("file " + pagePath + page + ".html not found");
        }
    }

    public ResponseHandler apply(MocoConfig config) {
        return null;
    }
}
