package com.paulhammant.greyangular;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OutgoingGetScheduleContentTest {

    @Test(groups = "unit")
    public void schedule_request_key_should_be_in_greyhound_dot_com_expected_json() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutgoingGetScheduleContent content = new OutgoingGetScheduleContent("abc123");
        content.writeTo(baos);
        String actual = baos.toString().replace("\"","'");
        assertThat(actual, is(
           "{'request': {'__type': 'Greyhound.Website." +
             "DataObjects.ClientScheduleDetailRequest','Key': 'abc123'}}"));
    }


}
