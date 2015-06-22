package com.paulhammant.greyangular.servlet;

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.HttpRequest;
import com.paulhammant.greyangular.Constants;
import com.paulhammant.greyangular.LocationMap;
import com.paulhammant.greyangular.LocationsByName;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractGreyhoundLocationsByName extends AbstractJsonpServlet implements LocationsByName {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        writeJsonpResponse(resp, req.getParameter("callback"),
                getGreyhoundLocations(req.getParameter("term"),
                        req.getSession().getAttribute(Constants.SESSION_KEY),
                        LocationMap.getOrMakeMap(req)));
    }

    public String getGreyhoundLocations(String term, Object ghSessionId, LocationMap locationMap)  {
        HttpRequest httpRequest = makeGreyhoundLocationPostRequest(term, ghSessionId);
        String json = null;
        try {
            json = httpRequest.execute().parseAs(String.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        json = json.replace(",\"Enabled\":true,\"Attributes\":{}", "");
        String locationsString = json.substring(json.indexOf("["), json.indexOf("]") + 1);
        locationMap.consumeIfNeeded(locationsString);
        return "(" + locationsString + ")";
    }

    private HttpRequest makeGreyhoundLocationPostRequest(final String term, Object ghSessionId)  {
        return postRequest(url("/services/locations.asmx/" + getGreyhoundServiceName()),
                new OutgoingLocationContent(term), ghSessionId);
    }

    public abstract String getGreyhoundServiceName();

    public static class OutgoingLocationContent extends AbstractHttpContent {
        private final byte[] content;

        public OutgoingLocationContent(String term) {
            super("application/json; charset=utf-8");
            content = ("{\n" +
                    "    \"context\": {\n" +
                    "        \"Text\": \"" + term + "\",\n" +
                    "        \"NumberOfItems\": 0\n" +
                    "    }\n" +
                    "}").getBytes();
        }

        @Override
        protected long computeLength() throws IOException {
            return content.length;
        }

        public void writeTo(OutputStream out) throws IOException {
            out.write(content);
        }
    }
}
