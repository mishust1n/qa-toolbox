package io.mishustin.qa;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HtmlTestReporter {

    private static final Logger LOG = LogManager.getLogger(HtmlTestReporter.class);

    private static final ThreadLocal<HtmlTestReporter> reporters = ThreadLocal.withInitial(() -> {
        LOG.debug("GET new test reporter instance from thread local");
        return new HtmlTestReporter();
    });

    private static Path reportFile;
    private static ExtentReports extent;
    private static Map<String, ExtentTest> testNodes;
    private static boolean isReportNotCreatedYet = true;
    private ExtentTest currentTest;

    private HtmlTestReporter() {
        LOG.debug("Create new reporter instance");
        createReportFile();
    }

    public static HtmlTestReporter getReporter() {
        LOG.debug("Get reporter");
        return reporters.get();
    }

    public static synchronized void createReportFile() {
        if (isReportNotCreatedYet) {
            String fileName = createFileName();

            createReportFolder();

            extent = createExtentReports(fileName);
            testNodes = new ConcurrentHashMap<>();
            isReportNotCreatedYet = false;
        }
    }

    private static String createFileName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return "report-" + LocalDateTime.now().format(formatter) + ".html";
    }

    private static void createReportFolder() {
        File reportsFolder = new File("reports");

        if (!reportsFolder.exists() && !reportsFolder.mkdir()) {
            throw new ToolboxException("Unable to create \"reports\" folder");
        }
    }

    private static ExtentReports createExtentReports(final String fileName) {

        reportFile = Paths.get("reports", fileName).toAbsolutePath();

        LOG.debug("Create test report file {}", reportFile);

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFile.toFile())
                .viewConfigurer()
                .viewOrder()
                .as(new ViewName[]{ViewName.TEST, ViewName.DASHBOARD, ViewName.EXCEPTION})
                .apply();

        ExtentReports extentReporter = new ExtentReports();
        extentReporter.attachReporter(sparkReporter);
        extentReporter.setAnalysisStrategy(AnalysisStrategy.CLASS);
        return extentReporter;
    }

    public void createTestClass(String className) {
        LOG.debug("Create test node: {}", className);
        testNodes.computeIfAbsent(className, e -> extent.createTest(e));
    }

    public void createTest(String testCase, String testName) {
        LOG.debug("Create test: {}-{}", testCase, testName);
        if (!testNodes.containsKey(testCase)) {
            throw new ToolboxException(String.format("Test case %s is not created! Use method createTestClass to create it", testCase));
        }

        currentTest = testNodes.get(testCase).createNode(testName);
    }

    public void pass(String testName) {
        LOG.debug("Log passed test: {}", testName);
        currentTest.pass("PASSED: " + testName);
    }

    public void skip(String testName, String reason) {
        currentTest.skip("SKIPPED: " + testName);
        currentTest.skip(reason);
    }

    public void skip(String testName, Throwable reason) {
        currentTest.skip("SKIPPED: " + testName);
        currentTest.skip(reason);
    }

    public void fail(String testName, Throwable throwable) {
        currentTest.fail("FAILED: " + testName);
        currentTest.fail(throwable);
    }

    public void log(String message) {
        LOG.debug("Write log message {}", message);
        currentTest.log(Status.INFO, message);
    }

    public synchronized void flush() {
        LOG.debug("Flush test report");
        LOG.info("Report file: {}", reportFile);
        extent.flush();
    }
}
