package com.paulhammant.greyangular;

import com.google.api.client.http.HttpResponseException;
import com.paulhammant.greyangular.servlet.EnsureHasAGreyhoundSessionFilter;
import com.paulhammant.greyangular.servlet.GetGreyhoundDestinationLocationsByName;
import com.paulhammant.greyangular.servlet.GetGreyhoundOriginLocationsByName;
import com.paulhammant.greyangular.servlet.Search;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.AssertJUnit.fail;

public class GreyhoundServicesTest {

    private LocationMap locationMap;

    @BeforeMethod(groups = "integration")
    public void setup() {
        locationMap = new LocationMap(new HashMap<String, String>());
    }

    @Test(groups = "integration")
    public void can_get_origins_from_greyhound_dot_com() throws IOException {
        GetGreyhoundOriginLocationsByName o = new GetGreyhoundOriginLocationsByName();

        String results = o.getGreyhoundLocations("Chickasha", null, locationMap);
        assertThat(results, equalTo("([{\"Text\":\"Chickasha, OK\",\"Value\":\"670494|Chickasha/OK\"}])"));

        results = o.getGreyhoundLocations("ZZUUZUZUUOIDEED", null, locationMap);
        assertThat(results, equalTo("([])"));
    }

    @Test(groups = "integration")
    public void can_get_destinations_from_greyhound_dot_com() throws IOException {
        GetGreyhoundDestinationLocationsByName o = new GetGreyhoundDestinationLocationsByName();

        String results = o.getGreyhoundLocations("Chickasha", null, locationMap);
        assertThat(results, equalTo("([{\"Text\":\"Chickasha, OK\",\"Value\":\"670494|Chickasha/OK\"}])"));

        results = o.getGreyhoundLocations("ZZUUZUZUUOIDEED", "", locationMap);
        assertThat(results, equalTo("([])"));
    }

    @Test(groups = "integration")
    public void sesson_initiated_at_greyhound_dot_com() throws IOException {
        EnsureHasAGreyhoundSessionFilter ss = new EnsureHasAGreyhoundSessionFilter();
        String session = ss.getSessionFromGreyhound();
        assertThat(session, notNullValue());
        assertThat(session.trim(), not(is("")));
        assertThat(session.length(), greaterThan(8));
    }

    @Test(groups = "integration")
    public void search_on_greyhound_dot_com_with_good_criteria_should_work() throws IOException {

        Search o = new Search();

        locationMap.consumeIfNeeded("[{\"Text\":\"New Baltimore, NY\",\"Value\":\"155397|New Baltimore/NY\"}]");
        locationMap.consumeIfNeeded("[{\"Text\":\"Chicago, IL\",\"Value\":\"560252|Chicago/IL\"}]");

        String session = new EnsureHasAGreyhoundSessionFilter().getSessionFromGreyhound();

        // TODO needs to not time out on Dec 1, 2013
        String results = o.doSearch("Chicago, IL", "New Baltimore, NY",
                "2013-12-01 07:25", session, locationMap);


        assertThat(results, containsString("SchedulesDepart"));

        // TODO deserialize to confirm expected JSON structure.

    }

    // Ignore as causing a 500 error on Greyhound.com isn't good.
    @Test(groups = "integration", enabled=false)
    public void search_on_greyhound_dot_com_with_bad_criteria_should_be_rejected() throws IOException {

        Search o = new Search();

        locationMap.consumeIfNeeded("[{\"Text\":\"New Bbbbb, NY\",\"Value\":\"222333|New Bbbbb/NY\"}]");
        locationMap.consumeIfNeeded("[{\"Text\":\"Ccccc, IL\",\"Value\":\"222334|Ccccc/IL\"}]");

        String session = new EnsureHasAGreyhoundSessionFilter().getSessionFromGreyhound();

        // TODO needs to not time out Dec 1, 2013
        try {
            String results = o.doSearch("Ccccc, IL", "New Bbbbb, NY",
                    "2013-12-01 07:25", session, locationMap);
            fail("should have barfed");
        } catch (HttpResponseException e) {
            assertThat(e.getMessage(), containsString("{\"Message\":\"There was an error processing the request.\",\"StackTrace\":\"\",\"ExceptionType\":\"\"}"));
        }
    }

}
