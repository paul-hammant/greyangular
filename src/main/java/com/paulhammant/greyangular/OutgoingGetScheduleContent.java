package com.paulhammant.greyangular;

import com.google.api.client.http.AbstractHttpContent;

import java.io.IOException;
import java.io.OutputStream;

public class OutgoingGetScheduleContent extends AbstractHttpContent {
    private final byte[] content;

    public OutgoingGetScheduleContent(String key) {
        super("application/json; charset=utf-8");

        String s =
                "{" +
                   "\"request\": {" +
                     "\"__type\": \"Greyhound.Website.DataObjects.ClientScheduleDetailRequest\"," +
                     "\"Key\": \"" + key + "\"" +
                   "}" +
                "}";

        content = s.getBytes();

    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(content);
    }
}
