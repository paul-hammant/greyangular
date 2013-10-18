package com.paulhammant.greyangular.moco;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.extractor.ParamRequestExtractor;
import com.github.dreamhead.moco.handler.AbstractContentResponseHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class GreyAbstractContentResponseHandler extends AbstractContentResponseHandler {

    protected String getParam(FullHttpRequest request, String param) {
        ParamRequestExtractor paramRequestExtractor = new ParamRequestExtractor(param);
        return paramRequestExtractor.extract(request).get();
    }

    // I don't know what this method is for.
    public final ResponseHandler apply(MocoConfig mocoConfig) {
        return null;
    }


}
