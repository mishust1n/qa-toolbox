package io.mishustin.qa;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HtmlReporterTest {

    @BeforeClass
    public void initReport() {
        HtmlTestReporter.createReportFile();
    }

    @Test
    public void createReportShouldCreateReportFile() {
        HtmlTestReporter.createReportFile();

        HtmlTestReporter htmlTestReporter = HtmlTestReporter.getReporter();

        htmlTestReporter.createTestClass("Test Class 1");
        htmlTestReporter.createTest("Test Class 1", "Test Test 1");
        htmlTestReporter.flush();
    }

    @Test
    public void runWithTheSingleThreadsShouldNotThrowExceptions() {
        simulateLogLifecycle();
    }

    @Test(threadPoolSize = 2, invocationCount = 2)
    public void runWithTheTwoThreadsShouldNotThrowExceptions() {
        simulateLogLifecycle();
    }

    @Test(threadPoolSize = 5, invocationCount = 15)
    public void runWithTheManyThreadsShouldNotThrowExceptions() {
        simulateLogLifecycle();
    }

    private void simulateLogLifecycle() {
        long threadId = Thread.currentThread().getId();
        HtmlTestReporter reporter = HtmlTestReporter.getReporter();

        reporter.createTestClass("Test Class " + threadId);
        reporter.createTest("Test Class " + threadId, "Test " + threadId);

        reporter.log("I`m from " + Thread.currentThread().getId());

        reporter.flush();
    }

}
