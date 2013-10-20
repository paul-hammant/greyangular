package com.paulhammant.greyangular;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import com.google.inject.Inject;
import com.paulhammant.greyangular.moco.PageResponse;
import com.paulhammant.greyangular.moco.SearchContentHandler;
import com.paulhammant.greyangular.selenium.SearchResultsComponent;
import com.paulhammant.greyangular.testng.OurModuleFactory;
import com.paulhammant.greyangular.testng.TestNumber;
import com.paulhammant.ngwebdriver.AngularModelAccessor;
import org.openqa.selenium.WebDriver;
import org.seleniumhq.selenium.fluent.FluentWebElements;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.google.common.base.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@Guice(moduleFactory = OurModuleFactory.class)
public class SearchResultsComponentTest {

    @Inject
    private WebDriver webDriver;

    @Inject
    private AngularModelAccessor ngModel;

    @Inject
    private TestNumber testNumber;

    private MocoHttpServer mocoHttpServer;
    private ActualHttpServer moco;

    @BeforeMethod(groups = "ui")
    public void setup() {
        moco = ActualHttpServer.createQuietServer(of(8080));
        mocoHttpServer = new MocoHttpServer(moco);
        mocoHttpServer.start();
    }

    @AfterMethod(groups = "ui")
    public void teardown() {
        mocoHttpServer.stop();
    }

    @Test(groups = "ui")
    public void bogus_params_forces_a_return_to_criteria_page() {

        moco.request(by(uri("/SearchResults.html"))).response(new PageResponse("SearchResults"));

        SearchResultsComponent component = new SearchResultsComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/SearchResults.html#1/2/3", webDriver);

        component.hasReturnedToCriteria();

    }

    @Test(groups = "ui")
    public void missing_params_forces_a_return_to_criteria_page() {

        moco.request(by(uri("/SearchResults.html"))).response(new PageResponse("SearchResults"));

        SearchResultsComponent component = new SearchResultsComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/SearchResults.html", webDriver);

        component.hasReturnedToCriteria();

    }

    @Test(groups = "ui")
    public void no_search_results_shows_suitable_error() {

        moco.request(by(uri("/SearchResults.html"))).response(new PageResponse("SearchResults"));

        final StringBuilder params = new StringBuilder();

        moco.request(by(uri("/Search")))
                .response(new SearchContentHandler() {
                    @Override
                    protected String getContent(String from, String to, String when) {
                        // would ordinarily do assertions here,
                        // but are in in the wrong thread.
                        params.append("from:").append(from)
                                .append("; to:").append(to)
                                .append("; when:").append(when);
                        return "{ \"Valid\": false }";
                    }
                });

        SearchResultsComponent component = new SearchResultsComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/SearchResults.html#Narnia/TX/Valhalla/OR/2013-09-27/08:22", webDriver, ngModel);

        assertThat(params.toString(),
                equalTo("from:Narnia, TX; to:Valhalla, OR; when:2013-09-27 08:22"));

        component.noResultsText().shouldBe("No Results for Narnia, TX to Valhalla, OR\non the date selected.");

    }

    @Test(groups = "ui")
    public void progress_bar_appears_before_results() throws InterruptedException {

        moco.request(by(uri("/SearchResults.html"))).response(new PageResponse("SearchResults"));

        SearchResultsComponent component = new SearchResultsComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/SearchResults.html#Narnia/TX/Valhalla/OR/2013-09-27/08:22", webDriver, ngModel);

        long percentage1 = component.getPercentage();

        assertThat(percentage1, greaterThanOrEqualTo(0L));

        Thread.sleep(200);

        long percentage2 = component.getPercentage();

        assertThat(percentage2, greaterThan(percentage1));

        Thread.sleep(400);

        long percentage3 = component.getPercentage();

        assertThat(percentage3, greaterThan(percentage2));


    }

    @Test(groups = "ui")
    public void for_search_results_clicking_selection_acts_as_radio_button() {

        moco.request(by(uri("/SearchResults.html"))).response(new PageResponse("SearchResults"));

        final StringBuilder params = new StringBuilder();

        moco.request(by(uri("/Search")))
                .response(new SearchContentHandler(){
                    @Override
                    protected String getContent(String from, String to, String when) {
                        // would ordinarily do assertions here,
                        // but are in in the wrong thread.
                        params.append("from:").append(from)
                                .append("; to:").append(to)
                                .append("; when:").append(when);
                        return "{\n" +
                                "  \"Valid\": true,\n" +
                                "  \"RT\": false,\n" +
                                "  \"DRM\": false,\n" +
                                "  \"RedirectUrl\": null,\n" +
                                "  \"RedirectMethod\": null,\n" +
                                "  \"RedirectData\": null,\n" +
                                "  \"Messages\": null,\n" +
                                "  \"DisplayDepartDate\": \"SATURDAY, SEPTEMBER 28, 2013\",\n" +
                                "  \"DisplayReturnDate\": null,\n" +
                                "  \"OriginCity\": \"Chicago\",\n" +
                                "  \"OriginState\": \"IL\",\n" +
                                "  \"DestinationCity\": \"Denver\",\n" +
                                "  \"DestinationState\": \"CO\",\n" +
                                "  \"KeyOutbound\": null,\n" +
                                "  \"KeyReturn\": null,\n" +
                                "  \"SchedulesDepart\": [\n" +
                                "    {\n" +
                                "      \"Key\": \"0|MzU1NzM2ODczNTkwMTQwOTpTb2NrMgAwAAAAMAAwAA==\",\n" +
                                "      \"DisplayDeparts\": \"01:30 AM<br/>Sat, 09/28\",\n" +
                                "      \"DisplayArrives\": \"11:40 PM<br/>Sat, 09/28\",\n" +
                                "      \"Time\": \"23H, 10M\",\n" +
                                "      \"Available\": true,\n" +
                                "      \"Transfers\": \"1\",\n" +
                                "      \"Fare1\": {\n" +
                                "        \"Key\": \"0|MzU1NzM2ODczNTkwMTQwOTpTb2NrMgAwAE5SLFJYADQAMQAxMzQuNDAAT0w=\",\n" +
                                "        \"Total\": \"$134.40\",\n" +
                                "        \"Available\": true,\n" +
                                "        \"Discount\": \"1\",\n" +
                                "        \"Limit\": 46\n" +
                                "      },\n" +
                                "      \"Fare2\": {\n" +
                                "        \"Key\": null,\n" +
                                "        \"Total\": null,\n" +
                                "        \"Available\": false,\n" +
                                "        \"Discount\": null,\n" +
                                "        \"Limit\": null\n" +
                                "      },\n" +
                                "      \"Fare3\": {\n" +
                                "        \"Key\": \"0|MzU1NzM2ODczNTkwMTQwOTpTb2NrMgAwAE5SADIAMwAxNzYuMDAAT0w=\",\n" +
                                "        \"Total\": \"$176.00\",\n" +
                                "        \"Available\": true,\n" +
                                "        \"Discount\": null,\n" +
                                "        \"Limit\": 46\n" +
                                "      },\n" +
                                "      \"Fare4\": {\n" +
                                "        \"Key\": \"0|MzU1NzM2ODczNTkwMTQwOTpTb2NrMgAwAAAwADIAMTk3LjAwAE9M\",\n" +
                                "        \"Total\": \"$197.00\",\n" +
                                "        \"Available\": true,\n" +
                                "        \"Discount\": null,\n" +
                                "        \"Limit\": 46\n" +
                                "      },\n" +
                                "      \"SeatsAvailable\": -1,\n" +
                                "      \"HasExpressSchedule\": false,\n" +
                                "      \"HasExpressScheduleUS\": \"\",\n" +
                                "      \"Sort\": [\n" +
                                "        23005530,\n" +
                                "        23006860,\n" +
                                "        1390,\n" +
                                "        1\n" +
                                "      ],\n" +
                                "      \"DepartureDate\": \"2013/09/28 01:30:00\"\n" +
                                "    }\n" +
                                "  ],\n" +
                                "  \"SchedulesReturn\": [],\n" +
                                "  \"Discounts\": [\n" +
                                "    [\n" +
                                "      \"1\",\n" +
                                "      \"Online Discount Applied\"\n" +
                                "    ]\n" +
                                "  ]\n" +
                                "}";
                    }
                });

        SearchResultsComponent searchResults = new SearchResultsComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/SearchResults.html#Chicago/IL/Denver/CO/2013-09-27/08:22", webDriver, ngModel);

        assertThat(searchResults.getSelection(), equalTo("null"));

        searchResults.tableText().shouldBe("01:30 AM\nSat, 09/28\nshow schedule 11:40 PM\nSat, 09/28 23H, 10M 1 $134.40 $176.00 $197.00");

        assertThat(params.toString(),
                equalTo("from:Chicago, IL; to:Denver, CO; when:2013-09-27 08:22"));

        FluentWebElements radioButtons = searchResults.radioButtons();

        // only three of the four are visible.
        assertThat(radioButtons.size(), is(4));
        radioButtons.get(0).isDisplayed().shouldBe(true);
        radioButtons.get(1).isDisplayed().shouldBe(false);
        radioButtons.get(2).isDisplayed().shouldBe(true);
        radioButtons.get(3).isDisplayed().shouldBe(true);

        // Click first radio button (Advanced Purchase)
        radioButtons.get(0).click();

        // Selection Model changes appropriately
        assertThat(searchResults.getSelection(),
                equalTo("{\"Key\":\"0|MzU1NzM2ODczNTkwMTQwOTpTb2NrMgAwAE5SLFJYADQAMQAxMzQuNDAAT0w=\",\"Total\":\"$134.40\",\"Available\":true,\"Discount\":\"1\",\"Limit\":46}"));

        // Only one checked button, that is the first one (which we intended)
        FluentWebElements checkedButtons = searchResults.checkedRadioButtons();
        assertThat(checkedButtons.size(), is(1));
        checkedButtons.get(0).getAttribute("data-ng-click").shouldContain("Fare1");

        // Click second radio button (Standard Fare)
        radioButtons.get(2).click();

        // Selection Model changes appropriately
        assertThat(searchResults.getSelection(),
                equalTo("{\"Key\":\"0|MzU1NzM2ODczNTkwMTQwOTpTb2NrMgAwAE5SADIAMwAxNzYuMDAAT0w=\",\"Total\":\"$176.00\",\"Available\":true,\"Discount\":null,\"Limit\":46}"));

        // Still only one checked button (checking radio nature) ...
        // ... and that is the third one
        checkedButtons = searchResults.checkedRadioButtons();
        assertThat(checkedButtons.size(), is(1));
        checkedButtons.get(0).getAttribute("data-ng-click").shouldContain("Fare3");

    }

}
