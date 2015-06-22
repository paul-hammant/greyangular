package com.paulhammant.greyangular;

import com.google.api.client.http.AbstractHttpContent;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.io.OutputStream;

public class OutgoingDoSearchContent extends AbstractHttpContent {
    private final byte[] content;

    public OutgoingDoSearchContent(String from, String to, String when, LocationMap locationMap) {
        super("application/json; charset=utf-8");

        String[] dateTimeParts = when.split(" ");

        DateTimeFormatter parser    = ISODateTimeFormat.dateTimeParser()
                .withChronology(ISOChronology.getInstanceUTC());
        DateTime actualWhen = parser.parseDateTime(dateTimeParts[0]);
        String outDate = DateTimeFormat.forPattern("dd MMMMM yyyy").print(actualWhen);

        String fromLcn = locationMap.valueFor(from);
        if (fromLcn == null) {
            throw new RuntimeException("no mapped 'from' location for " + from);
        }

        String toLcn = locationMap.valueFor(to);
        if (toLcn == null) {
            throw new RuntimeException("no mapped 'to' location for " + to);
        }

        String s = "{" +
                "\"request\":{" +
                "\"__type\":\"Greyhound.Website.DataObjects.ClientSearchRequest\"," +
                "\"Mode\":0," +
                "\"Origin\":\"" + fromLcn + "\"," +
                "\"Destination\":\"" + toLcn + "\"," +
                "\"Departs\":\"" + outDate + "\"," +
                "\"Returns\":null," +
                "\"TimeDeparts\":" + dateTimeParts[1].substring(0, 2) + "," +
                "\"TimeReturns\":null," +
                "\"RT\":false," +
                "\"Adults\":1," +
                "\"Seniors\":0," +
                "\"Children\":0," +
                "\"PromoCode\":\"\"," +
                "\"DiscountCode\":\"\"," +
                "\"Card\":\"\"," +
                "\"CardExpiration\":\"09/2018\"" +
                "}" +
                "}";

        content = s.getBytes();

    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(content);
    }
}
