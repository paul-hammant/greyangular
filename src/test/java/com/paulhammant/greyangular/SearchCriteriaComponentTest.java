package com.paulhammant.greyangular;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import com.google.inject.Inject;
import com.paulhammant.greyangular.moco.FsCachingLocationsByName;
import com.paulhammant.greyangular.moco.LocationContentHandler;
import com.paulhammant.greyangular.moco.PageResponse;
import com.paulhammant.greyangular.selenium.BaseFluentSeleniumPage;
import com.paulhammant.greyangular.selenium.SearchCriteriaComponent;
import com.paulhammant.greyangular.servlet.GetGreyhoundDestinationLocationsByName;
import com.paulhammant.greyangular.servlet.GetGreyhoundOriginLocationsByName;
import com.paulhammant.greyangular.testng.OurModuleFactory;
import com.paulhammant.greyangular.testng.TestNumber;
import com.paulhammant.ngwebdriver.AngularModelAccessor;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.google.common.base.Optional.of;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.openqa.selenium.By.id;

@Guice(moduleFactory = OurModuleFactory.class)
public class SearchCriteriaComponentTest {

    @Inject
    private WebDriver webDriver;

    @Inject
    private AngularModelAccessor ngModel;

    @Inject
    private TestNumber testNumber;

    private MocoHttpServer mocoHttpServer;
    private ActualHttpServer moco;
    private LocationMap locationMap;

    @BeforeMethod(groups = "ui")
    public void setup() {
        moco = ActualHttpServer.createQuietServer(of(8080));
        mocoHttpServer = new MocoHttpServer(moco);
        mocoHttpServer.start();
        locationMap = new LocationMap(new HashMap<String, String>());
    }

    @AfterMethod(groups = "ui")
    public void teardown() {
        mocoHttpServer.stop();
    }

    @Test(groups = "ui", enabled = false)
    public void datepicker_interaction_changes_search_model() {

        moco.request(by(uri("/SearchCriteria.html"))).response(new PageResponse("SearchCriteria"));
        SearchCriteriaComponent searchCriteria = new SearchCriteriaComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/SearchCriteria.html", webDriver, ngModel);
        ISOChronology utc = ISOChronology.getInstanceUTC();
        DateTimeFormatter isoDateTimeParser = ISODateTimeFormat.dateTimeParser().withChronology(utc);
        DateTime startDateTime = isoDateTimeParser.parseDateTime(searchCriteria.getDateTime());

        searchCriteria.calPopupButton().click();
        searchCriteria.tomorrow(startDateTime.dayOfMonth().get()).click();

        DateTime changedDateTime = isoDateTimeParser.parseDateTime(searchCriteria.getDateTime());

        assertThat(Hours.hoursBetween(startDateTime, changedDateTime).getHours(), anyOf(is(23), is(24)));

    }

    @Test(groups = "ui")
    public void time_interaction_changes_search_model() {

        moco.request(by(uri("/SearchCriteria.html"))).response(new PageResponse("SearchCriteria"));
        SearchCriteriaComponent searchCriteria = new SearchCriteriaComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/SearchCriteria.html", webDriver, ngModel);
        ISOChronology utc = ISOChronology.getInstanceUTC();
        DateTimeFormatter isoDateTimeParser = ISODateTimeFormat.dateTimeParser().withChronology(utc);
        DateTime startDateTime = isoDateTimeParser.parseDateTime(searchCriteria.getDateTime());

        searchCriteria.calPopupButton().click();
        searchCriteria.hourIncrementor().click().click(); // two hours

        DateTime changedDateTime = isoDateTimeParser.parseDateTime(searchCriteria.getDateTime());
        assertThat(Minutes.minutesBetween(startDateTime, changedDateTime).getMinutes(), between(119, 120));

    }

    private Matcher<Integer> between(int from, int to) {
        return is(both(greaterThanOrEqualTo(from)).and(lessThanOrEqualTo(to)));
    }

    @Test(groups = "ui")
    public void clicking_search_button_runs_though_validations() {

        moco.request(by(uri("/SearchCriteria.html")))
                .response(new PageResponse("SearchCriteria"));
        moco.request(by(uri("/GetOriginLocationsByName")))
                .response(new LocationContentHandler(new FsCachingLocationsByName(new GetGreyhoundOriginLocationsByName(), "Origin"), locationMap));
        moco.request(by(uri("/GetDestinationLocationsByName")))
                .response(new LocationContentHandler(new FsCachingLocationsByName(new GetGreyhoundDestinationLocationsByName(), "Destination"), locationMap));

        moco.request(by(uri("/SearchResults.html"))).response(new PageResponse("OK"));

        SearchCriteriaComponent searchCriteria = new SearchCriteriaComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/SearchCriteria.html", webDriver, ngModel);

        searchCriteria.clickSubmitButton();

        searchCriteria.originErrText().shouldBe("☜ Must enter a valid origin");
        searchCriteria.destinationErrText().shouldBe("☜ Must enter a valid destination");

        searchCriteria.originField().click().clearField().sendKeys("qqq");
        searchCriteria.destinationField().click().clearField().sendKeys("zzz");

        searchCriteria.clickSubmitButton();

        searchCriteria.originErrText().shouldBe("☜ Must enter a valid origin");
        searchCriteria.destinationErrText().shouldBe("☜ Must enter a valid destination");

        searchCriteria.originField().click().clearField().sendKeys("chic");
        searchCriteria.destinationField().click().clearField().sendKeys("new b");

        searchCriteria.clickSubmitButton();

        searchCriteria.originErrText().shouldBe("☜ Must enter a valid origin");
        searchCriteria.destinationErrText().shouldBe("☜ Must enter a valid destination");

        searchCriteria.originField().click().clearField().sendKeys("chic");
        searchCriteria.selectFirstOriginOffered();
        searchCriteria.destinationField().click().clearField().sendKeys("new b");
        searchCriteria.selectFirstDestinationOffered();

        BaseFluentSeleniumPage okPage = searchCriteria.clickSubmitButton();

        okPage.verifyOnPage();

        okPage.url().shouldMatch("SearchResults.html#Chicago/IL/New%20Baltimore/NY/\\d\\d\\d\\d-\\d\\d-\\d\\d/\\d\\d:\\d\\d");

    }

    @Test(groups = "ui")
    public void origin_and_destination_interaction_changes_search_model() {

        moco.request(by(uri("/SearchCriteria.html")))
                .response(new PageResponse("SearchCriteria"));

        final String site = "http://t" + testNumber.nextTestNum() + ".dev:8080";

        moco.request(by(uri("/GetOriginLocationsByName")))
                .response(new LocationContentHandler(new FsCachingLocationsByName(new GetGreyhoundOriginLocationsByName(), "Origin"), locationMap));

        moco.request(by(uri("/GetDestinationLocationsByName")))
                .response(new LocationContentHandler(new FsCachingLocationsByName(new GetGreyhoundDestinationLocationsByName(), "Destination"), locationMap));

        SearchCriteriaComponent component = new SearchCriteriaComponent(site + "/SearchCriteria.html", webDriver, ngModel);

        WebElement webElement = component.div(id("originRow")).getWebElement();

        component.setWhen("undefined");

        String searchJson = component.getSearch();

        assertThat(searchJson, equalTo("{}"));

        // Select Chicago for Origin
        component.originField().click().clearField().sendKeys("chic");
        component.selectFirstOriginOffered();

        // Select New Baltimore for Destination
        component.destinationField().click().clearField().sendKeys("new b");
        component.selectFirstDestinationOffered();

        searchJson = component.getSearch();

        assertThat(searchJson, equalTo("{'destination':{'Text':'New Baltimore, NY','Value':'155397|New Baltimore/NY'},'origin':{'Text':'Chicago, IL','Value':'560252|Chicago/IL'}}"));


    }

}
