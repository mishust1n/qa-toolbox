package io.mishustin.qa;

import org.testng.*;

public class TestNgListener implements ITestListener, IReporter, IClassListener {

    private final TestStepProcessor executionProcessor = new TestStepProcessor();

    @Override
    public void onBeforeClass(ITestClass testClass) {
        executionProcessor.processTestClassStart(getTestClassName(testClass));
    }

    @Override
    public void onTestStart(ITestResult result) {
        executionProcessor.processTestStart(getTestClassName(result), getTestName(result));
    }

    @Override
    public void onFinish(ITestContext context) {
        executionProcessor.processExecutionFinish();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        executionProcessor.processPassedTest(getTestName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        executionProcessor.processPassedTest(getTestName(result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        executionProcessor.processSkippedTest(getTestName(result), result.getThrowable());
    }

    private String getTestClassName(ITestClass testClass) {
        return testClass.getName();
    }

    private String getTestClassName(ITestResult result) {
        return result.getTestClass().getName();
    }

    private String getTestName(ITestResult result) {
        return result.getName();
    }

}