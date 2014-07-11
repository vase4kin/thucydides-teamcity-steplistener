package com.github.crystalservice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Format test description using regex patterns
 */
public class TestDescriptionFormatter {

    private static final String SIMPLE_TEST_DESCRIPTION_REGEX_PATTERN = "(.*): <span class='step-parameter'>(?!ExamplesTable)(.*)</span>";
    private static final String EXAMPLES_TABLE_TEST_DESCRIPTION_REGEX_PATTERN = "(.*): <span class='step-parameter'>ExamplesTable\\[tableAsString=(.*),headerSeparator.*";

    public static String formatTestDescription(String testDescription) {
        Matcher simpleTestDescriptionMatcher = new TestDescriptionFormatter().getSimpleTestDescriptionMatcher(testDescription);
        Matcher examplesTableTestDescriptionMatcher = new TestDescriptionFormatter().getExamplesTableTestDescriptionMatcher(testDescription);
        if(simpleTestDescriptionMatcher.matches()) {
            return String.format("%s: {%s}", simpleTestDescriptionMatcher.group(1), simpleTestDescriptionMatcher.group(2));
        } else if(examplesTableTestDescriptionMatcher.matches()) {
            return String.format("%s:\r\n%s", examplesTableTestDescriptionMatcher.group(1), examplesTableTestDescriptionMatcher.group(2)).replace("|n|", "|\r\n|");
        } else {
            return testDescription;
        }
    }

    private Matcher getSimpleTestDescriptionMatcher(String testDescription) {
        return getTestDescriptionMatcher(testDescription, SIMPLE_TEST_DESCRIPTION_REGEX_PATTERN);
    }

    private Matcher getExamplesTableTestDescriptionMatcher(String testDescription) {
        return getTestDescriptionMatcher(testDescription, EXAMPLES_TABLE_TEST_DESCRIPTION_REGEX_PATTERN);
    }

    private Matcher getTestDescriptionMatcher(String testDescription, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        return pattern.matcher(testDescription);
    }
}
