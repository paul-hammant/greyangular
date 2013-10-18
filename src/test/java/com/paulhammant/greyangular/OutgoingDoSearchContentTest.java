package com.paulhammant.greyangular;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OutgoingDoSearchContentTest {

    private LocationMap locationMap;
    private ByteArrayOutputStream baos;

    @BeforeMethod(groups = "unit")
    public void setUp() throws Exception {
        locationMap = new LocationMap(new HashMap<String, String>() {{
            put("AAA", "222333|AAA/NY");
            put("BBB", "222334|BBB/IL");
        }});
        baos = new ByteArrayOutputStream();
    }

    @Test(groups = "unit")
    public void search_json_for_greyhound_dot_com_should_work() throws IOException {

        OutgoingDoSearchContent content = new OutgoingDoSearchContent("AAA", "BBB", "2013-12-01 07:25", locationMap);
        content.writeTo(baos);
        String actual = baos.toString().replace("\"","'");
        assertThat(actual, is(
                "{'request':{'__type':'Greyhound.Website.DataObjects.ClientSearchRequest'," +
                        "'Mode':0,'Origin':'222333|AAA/NY','Destination':'222334|BBB/IL'," +
                        "'Departs':'01 December 2013','Returns':null,'TimeDeparts':07," +
                        "'TimeReturns':null,'RT':false,'Adults':1,'Seniors':0,'Children':0," +
                        "'PromoCode':'','DiscountCode':'','Card':'','CardExpiration':'09/2013'}}"));

    }

    @Test(groups = "unit")
    public void bad_from_location_should_error() throws IOException {

        try {
            new OutgoingDoSearchContent("XXX", "BBB", "2013-12-01 07:25", locationMap);
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("no mapped 'from' location for XXX"));
        }

    }

    @Test(groups = "unit")
    public void bad_to_location_should_error() throws IOException {

        try {
            new OutgoingDoSearchContent("AAA", "XXX", "2013-12-01 07:25", locationMap);
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("no mapped 'to' location for XXX"));
        }

    }
}
