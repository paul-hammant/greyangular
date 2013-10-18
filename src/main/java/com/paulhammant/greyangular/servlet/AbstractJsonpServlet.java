package com.paulhammant.greyangular.servlet;

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.paulhammant.greyangular.Constants;
import com.paulhammant.greyangular.StringResponse;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractJsonpServlet extends HttpServlet {

    protected GenericUrl url(final String url) {
        return new GenericUrl("https://www.greyhound.com" + url);
    }

    protected void writeJsonpResponse(HttpServletResponse resp, String callback, String result) throws IOException {
        resp.setStatus(200);
        resp.setContentType("text/plain");
        resp.getOutputStream().write((callback + " ").getBytes());
        resp.getOutputStream().write(("(" + result + ")").getBytes());
    }

    protected HttpRequest postRequest(GenericUrl url, AbstractHttpContent content, Object ghSessionId) throws IOException {
        return addHeaders(new NetHttpTransport()
                .createRequestFactory(new StringResponse())
                .buildPostRequest(url, content), ghSessionId);
    }

    protected HttpRequest getRequest(GenericUrl url, Object ghSessionId) throws IOException {
        return addHeaders(new NetHttpTransport()
                .createRequestFactory(new StringResponse())
                .buildGetRequest(url), ghSessionId);
    }

    private HttpRequest addHeaders(HttpRequest req, Object ghSessionId) {
        HttpHeaders headers = req.getHeaders();
        headers.setUserAgent(Constants.USER_AGENT);
        headers.set("Referer", "http://www.greyhound.com");
        if (ghSessionId != null) {
            headers.setCookie("ASP.NET_SessionId=" + ghSessionId + ";");
        }
        return req;
    }



}
