package com.paulhammant.greyangular;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import com.google.inject.Inject;
import com.paulhammant.greyangular.moco.PageResponse;
import com.paulhammant.greyangular.moco.ScheduleContentHandler;
import com.paulhammant.greyangular.selenium.ShowScheduleComponent;
import com.paulhammant.greyangular.testng.OurModuleFactory;
import com.paulhammant.greyangular.testng.TestNumber;
import com.paulhammant.ngwebdriver.AngularModelAccessor;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.google.common.base.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Guice(moduleFactory = OurModuleFactory.class)
public class ShowScheduleComponentTest {

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
    public void schedule_can_be_shown() {

        moco.request(by(uri("/ShowSchedule.html"))).response(new PageResponse("ShowSchedule"));

        final StringBuilder params = new StringBuilder();

        moco.request(by(uri("/GetScheduleDetails")))
                .response(new ScheduleContentHandler() {
                    @Override
                    protected String getContent(String key) {
                        params.append("key:").append(key);
                        return "[{'Schedule':'999','Arrives':'xx1','Departs':'10:40 PM','ArrivesDateTime':'xx2','DepartsDateTime':'xx3','Layover':'xx4','Location':'(START) - NARNIA, TX','Carrier':'BTW','Meal':'xx5'},\n" +
                                "{'Schedule':'999','Arrives':'11:59 PM','Departs':'11:59 PM','ArrivesDateTime':'xx6','DepartsDateTime':'xx7','Layover':'xx8','Location':'ATLANTIS, NJ','Carrier':'BTW','Meal':'xx9'}]".replace("'", "\"");
                    }
                });

        ShowScheduleComponent schedule = new ShowScheduleComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/ShowSchedule.html#abc123", webDriver);

        assertThat(params.toString(), equalTo("key:__HASH__abc123"));

        schedule.tableText().shouldBe(
                "(START) - NARNIA, TX xx1 10:40 PM xx4 BTW xx5 999\n" +
                "ATLANTIS, NJ 11:59 PM 11:59 PM xx8 BTW xx9 999");

    }

    @Test(groups = "ui")
    public void no_schedule_shows_suitable_error() {

        moco.request(by(uri("/ShowSchedule.html"))).response(new PageResponse("ShowSchedule"));

        final StringBuilder params = new StringBuilder();

        moco.request(by(uri("/GetScheduleDetails")))
                .response(new ScheduleContentHandler() {
                    @Override
                    protected String getContent(String key) {
                        params.append("key:").append(key);
                        return "{ \"Valid\": false }";
                    }
                });

        ShowScheduleComponent component = new ShowScheduleComponent("http://t" + testNumber.nextTestNum() + ".dev:8080/ShowSchedule.html#abc123", webDriver);

        assertThat(params.toString(), equalTo("key:__HASH__abc123"));

        component.noResultsText().shouldBe("No Schedule Available. Perhaps Start Your Search Again.");

    }


}
