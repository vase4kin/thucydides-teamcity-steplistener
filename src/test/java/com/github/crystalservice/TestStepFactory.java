package com.github.crystalservice;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;

import static net.thucydides.core.model.TestResult.*;

public class TestStepFactory {

    public static TestStep getSuccessfulTestStep(String description) {
        return createNewTestStep(description, SUCCESS);
    }

    public static TestStep getFailureTestStep(String description, Throwable assertionError) {
        return createNewTestStep(description, FAILURE, assertionError);
    }

    public static TestStep getFailureTestStep(String description) {
        return createNewTestStep(description, FAILURE);
    }

    public static TestStep getErrorTestStep(String description, Throwable assertionError) {
        return createNewTestStep(description, ERROR, assertionError);
    }

    public static TestStep getErrorTestStep(String description) {
        return createNewTestStep(description, ERROR);
    }

    public static TestStep getSkippedTestStep(String description) {
        return createNewTestStep(description, SKIPPED);
    }

    public static TestStep getIgnoredTestStep(String description) {
        return createNewTestStep(description, IGNORED);
    }

    public static TestStep getPendingTestStep(String description) {
        return createNewTestStep(description, PENDING);
    }

    public static TestStep createNewTestStep(String description, TestResult result, Throwable assertionError) {
        TestStep step = new TestStep(description);
        step.setResult(result);
        step.testAborted(assertionError);
        step.setDuration(100);
        return step;
    }

    public static TestStep createNewTestStep(String description, TestResult result) {
        TestStep step = new TestStep(description);
        step.setResult(result);
        step.setDuration(100);
        return step;
    }
}
