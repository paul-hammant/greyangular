package com.paulhammant.greyangular.moco;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AbstractContentResponseHandler;
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

    protected void writeContentResponse(FullHttpRequest fullHttpRequest, ByteBuf byteBuf) {
        try {
            File file = new File(pagePath, page + ".html");
            byteBuf.writeBytes(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            byteBuf.writeBytes(("file " + pagePath + page + ".html not found").getBytes());
        }
    }

    public ResponseHandler apply(MocoConfig config) {
        return null;
    }
}
