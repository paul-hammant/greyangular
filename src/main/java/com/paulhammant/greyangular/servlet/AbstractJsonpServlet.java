package com.paulhammant.greyangular.servlet;

import com.google.api.client.http.*;
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
        System.err.println(">> " + callback + " <> " + result);
    }

    protected HttpRequest postRequest(GenericUrl url, AbstractHttpContent content, Object ghSessionId) {
        HttpRequestFactory requestFactory = new NetHttpTransport()
                .createRequestFactory(new StringResponse());
        try {
            return addHeaders(requestFactory.buildPostRequest(url, content), ghSessionId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
