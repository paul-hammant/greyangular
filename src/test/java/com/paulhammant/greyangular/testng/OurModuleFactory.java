package com.paulhammant.greyangular.testng;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.paulhammant.ngwebdriver.AngularModelAccessor;
import com.thoughtworks.proxy.factory.CglibProxyFactory;
import com.thoughtworks.proxy.toys.nullobject.Null;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.IModuleFactory;
import org.testng.ITestContext;

import java.util.Arrays;

public class OurModuleFactory
        implements IModuleFactory {

    private static Module module;

    public Module createModule(ITestContext context,
                               Class<?> testClass) {
        if (module != null) {
            return module;
        }


        String[] groups = context.getIncludedGroups();

        if (groups.length == 0 || Arrays.asList(groups).contains("ui")) {
            module = new Module() {
                public void configure(Binder binder) {
                    FirefoxDriver ffd = new FirefoxDriver();
                    binder.bind(WebDriver.class).toInstance(ffd);
                    binder.bind(TestNumber.class).toInstance(new TestNumber());
                    binder.bind(AngularModelAccessor.class).toInstance(new OurAngularModelAccessor(ffd));
                }
            };
        } else {
            // integration tests without ui tests
            module = new Module() {
                public void configure(Binder binder) {
                    bindNullInstance(binder, WebDriver.class);
                    bindNullInstance(binder, TestNumber.class);
                    bindNullInstance(binder, AngularModelAccessor.class);
                }
            };
        }
        return module;
    }

    private final CglibProxyFactory cglib = new CglibProxyFactory();

    private void bindNullInstance(Binder binder, Class aClass) {
        binder.bind(aClass).toInstance(Null.proxy(aClass).build(cglib));
    }
}
