package io.mishustin.qa;

public class TestStepProcessor {

    public void processPassedTest(String testName) {
        HtmlTestReporter.getReporter().pass(testName);
    }

    public void processFailedTest(String testName, Exception reason, boolean isXfail) {
        if (isXfail) {
            HtmlTestReporter.getReporter().fail(testName, reason);
        } else {
            HtmlTestReporter.getReporter().skip(testName, reason);
        }
    }

    public void processSkippedTest(String testName, Throwable reason) {
        HtmlTestReporter.getReporter().skip(testName, reason);
    }

    public void processTestClassStart(String testClassName) {
        HtmlTestReporter.getReporter().createTestClass(testClassName);
    }

    public void processTestStart(String testClassName, String testName) {
        HtmlTestReporter.getReporter().createTest(testClassName, testName);
    }

    public void processExecutionFinish() {
        HtmlTestReporter.getReporter().flush();
    }

}
