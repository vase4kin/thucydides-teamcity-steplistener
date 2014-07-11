package com.github.crystalservice;

import junit.framework.TestCase;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.slf4j.Logger;

import java.util.HashMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class to test implemented team city thucydides step listener
 */
public class TeamCityStepListenerTest {

    @Mock
    private Logger logger;

    @Mock
    private DataTable dataTable;

    private TeamCityStepListener teamCityStepListener;

    private static final String STORY_PATH = "stories/sprint-1/us-1/story.story";
    private static final Story STORY = Story.withIdAndPath("storyId", "Test story", STORY_PATH);
    private static final Throwable THROWABLE = new Throwable("the test is failed!");

    @Before
    public void before() {
        initMocks(this);
        teamCityStepListener = spy(new TeamCityStepListener(logger));
        doReturn("StackTrace").when(teamCityStepListener).getStackTrace(any(Throwable.class));
    }

    @Test
    public void testScenarioResultIsSuccess() {

        TestOutcome testOutcome = new TestOutcome("passedScenario");
        testOutcome.setUserStory(STORY);
        testOutcome.recordStep(TestStepFactory.getSuccessfulTestStep("Passed"));

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.passedScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.passedScenario']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(2)).info(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testFinishedExpectedMessage));
    }

    @Test
    public void testScenarioResultIsFailure() {

        TestOutcome testOutcome = new TestOutcome("failedScenario");
        testOutcome.setUserStory(STORY);
        testOutcome.recordStep(TestStepFactory.getFailureTestStep("Failed scenario step", THROWABLE));
        testOutcome.setTestFailureCause(THROWABLE);

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.failedScenario']";
        String testFailedExpectedMessage = "##teamcity[testFailed  message='the test is failed!' details='Steps:|r|nFailed scenario step (0.1) -> ERROR|r|n|nStackTrace|r|n|r|n' name='sprint-1.us-1.story.failedScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.failedScenario']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(3)).info(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testFailedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(2), is(testFinishedExpectedMessage));
    }

    @Test
    public void testScenarioChildStepResultIsFailure() {

        TestOutcome testOutcome = new TestOutcome("failedScenario");
        testOutcome.setUserStory(STORY);
        TestStep testStep = TestStepFactory.getFailureTestStep("Failed scenario step");
        testStep.addChildStep(TestStepFactory.getFailureTestStep("Failed scenario child step", THROWABLE));
        testOutcome.recordStep(testStep);
        testOutcome.setTestFailureCause(THROWABLE);

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.failedScenario']";
        String testFailedExpectedMessage = "##teamcity[testFailed  message='the test is failed!' details='Steps:|r|nFailed scenario step (0.1) -> ERROR|r|n|nChildren Steps:|r|nFailed scenario child step (0.1) -> ERROR|r|n|nStackTrace|r|n|r|n|r|n|r|n' name='sprint-1.us-1.story.failedScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.failedScenario']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(3)).info(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testFailedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(2), is(testFinishedExpectedMessage));
    }

    @Test
    public void testScenarioResultIsError() {

        TestOutcome testOutcome = new TestOutcome("failedScenario");
        testOutcome.setUserStory(STORY);
        testOutcome.recordStep(TestStepFactory.getErrorTestStep("Failed scenario step", THROWABLE));
        testOutcome.setTestFailureCause(THROWABLE);

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.failedScenario']";
        String testFailedExpectedMessage = "##teamcity[testFailed  message='the test is failed!' details='Steps:|r|nFailed scenario step (0.1) -> ERROR|r|n|nStackTrace|r|n|r|n' name='sprint-1.us-1.story.failedScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.failedScenario']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(3)).info(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testFailedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(2), is(testFinishedExpectedMessage));
    }

    @Test
    public void testScenarioResultIsSkipped() {

        TestOutcome testOutcome = new TestOutcome("skippedScenario");
        testOutcome.setUserStory(STORY);
        testOutcome.recordStep(TestStepFactory.getSkippedTestStep("Skipped scenario step"));

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.skippedScenario']";
        String testIgnoredExpectedMessage = "##teamcity[testIgnored  name='sprint-1.us-1.story.skippedScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.skippedScenario']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(3)).info(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testIgnoredExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(2), is(testFinishedExpectedMessage));
    }

    @Test
    public void testScenarioResultIsPending() {

        TestOutcome testOutcome = new TestOutcome("pendingScenario");
        testOutcome.setUserStory(STORY);
        testOutcome.recordStep(TestStepFactory.getPendingTestStep("Pending scenario step"));

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.pendingScenario']";
        String testIgnoredExpectedMessage = "##teamcity[testIgnored  name='sprint-1.us-1.story.pendingScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.pendingScenario']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(3)).info(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testIgnoredExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(2), is(testFinishedExpectedMessage));
    }

    @Test
    public void testScenarioResultIsIgnored() {

        TestOutcome testOutcome = new TestOutcome("ignoringScenario");
        testOutcome.setUserStory(STORY);
        testOutcome.recordStep(TestStepFactory.getIgnoredTestStep("Ignored scenario step"));

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.ignoringScenario']";
        String testIgnoredExpectedMessage = "##teamcity[testIgnored  name='sprint-1.us-1.story.ignoringScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.ignoringScenario']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(3)).info(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testIgnoredExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(2), is(testFinishedExpectedMessage));
    }

    @Test
    public void testParametrisedSuccessfulScenarioWithGivenStoriesInStory() {

        teamCityStepListener.exampleStarted(new HashMap<String, String>() {{
            put("value", "exampleTableValue");
        }});

        TestOutcome testOutcome = new TestOutcome("parametrisedScenario");
        testOutcome.useExamplesFrom(dataTable);
        testOutcome.setUserStory(STORY);
        TestStep testStep = TestStepFactory.getSuccessfulTestStep("Successful scenario step");
        testStep.addChildStep(TestStepFactory.getSuccessfulTestStep("Successful scenario child step"));
        testOutcome.recordStep(testStep);
        TestStep testStep2 = TestStepFactory.getSuccessfulTestStep("[1] {value=exampleTableValue");
        testStep2.addChildStep(TestStepFactory.getSuccessfulTestStep("Successful scenario child step"));
        testOutcome.recordStep(testStep2);

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.parametrisedScenario.{value=exampleTableValue}']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.parametrisedScenario.{value=exampleTableValue}']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(2)).info(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testFinishedExpectedMessage));
    }

    @Test
    public void testEscapingSymbols() {

        TestOutcome testOutcome = new TestOutcome("\\|'\n\r\\[\\][]");
        testOutcome.setUserStory(STORY);
        Throwable throwable = new Throwable("\\|'\n\r\\[\\][]");
        testOutcome.recordStep(TestStepFactory.getFailureTestStep("\\|'\n\r\\[\\][]", throwable));
        testOutcome.setTestFailureCause(throwable);

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.|||'|n|r||[||]|[|]']";
        String testFailedExpectedMessage = "##teamcity[testFailed  message='|||'|n|r||[||]|[|]' details='Steps:|r|n|||'|n|r||[||]|[|] (0.1) -> ERROR|r|n|nStackTrace|r|n|r|n' name='sprint-1.us-1.story.|||'|n|r||[||]|[|]']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.|||'|n|r||[||]|[|]']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(3)).info(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testFailedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(2), is(testFinishedExpectedMessage));
    }

    @Test
    public void messagePropertyIsNullIfResultHasTestFailureCauseInstanceOfNPE() {

        TestOutcome testOutcome = new TestOutcome("failedScenario");
        testOutcome.setUserStory(STORY);
        testOutcome.recordStep(TestStepFactory.getFailureTestStep("Failed scenario step", new NullPointerException()));
        testOutcome.setTestFailureCause(new NullPointerException());

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='sprint-1.us-1.story.failedScenario']";
        String testFailedExpectedMessage = "##teamcity[testFailed  message='' details='Steps:|r|nFailed scenario step (0.1) -> ERROR|r|n|nStackTrace|r|n|r|n' name='sprint-1.us-1.story.failedScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='sprint-1.us-1.story.failedScenario']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(3)).info(stringArgumentCaptor.capture());

        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testFailedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(2), is(testFinishedExpectedMessage));
    }

    @Test
    public void testSuiteIsAStoryClass() {

        teamCityStepListener.testSuiteStarted(TestCase.class);
        teamCityStepListener.testSuiteFinished();

        String testSuiteStartedExpectedMessage = "##teamcity[testSuiteStarted  name='junit.framework.TestCase']";
        String testSuiteFinishedExpectedMessage = "##teamcity[testSuiteFinished  name='junit.framework.TestCase']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(2)).info(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testSuiteStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testSuiteFinishedExpectedMessage));
    }

    @Test
    public void testSuiteIsAStory() {

        teamCityStepListener.testSuiteStarted(STORY);
        teamCityStepListener.testSuiteFinished();

        String testSuiteStartedExpectedMessage = "##teamcity[testSuiteStarted  name='Test story']";
        String testSuiteFinishedExpectedMessage = "##teamcity[testSuiteFinished  name='Test story']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(2)).info(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testSuiteStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testSuiteFinishedExpectedMessage));
    }

    @Test
    public void testResultTitleIfPathIsDifferent() {

        String storyPath = "jbehave/stories/consult_dictionary/LookupADefinition.story";
        Story story = Story.withIdAndPath("storyId", "Test story", storyPath);

        TestOutcome testOutcome = new TestOutcome("passedScenario");
        testOutcome.setUserStory(story);
        testOutcome.recordStep(TestStepFactory.getSuccessfulTestStep("Passed"));

        teamCityStepListener.testFinished(testOutcome);

        String testStartedExpectedMessage = "##teamcity[testStarted  name='consult_dictionary.LookupADefinition.passedScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='consult_dictionary.LookupADefinition.passedScenario']";

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(2)).info(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testFinishedExpectedMessage));
    }

    @Test
    public void duplicatedTestSuitePrintMessageCallsIfTestSuiteIsAClassAndTestSuiteContainsMoreThanOneTest() {

        TestOutcome testOutcome = new TestOutcome("passedScenario", TestCase.class);
        testOutcome.recordStep(TestStepFactory.getSuccessfulTestStep("Passed"));

        TestOutcome testOutcome2 = new TestOutcome("passedScenario2", TestCase.class);
        testOutcome2.recordStep(TestStepFactory.getSuccessfulTestStep("Passed"));

        String testSuiteStartedExpectedMessage = "##teamcity[testSuiteStarted  name='junit.framework.TestCase']";
        String testSuiteFinishedExpectedMessage = "##teamcity[testSuiteFinished  name='junit.framework.TestCase']";

        String testStartedExpectedMessage = "##teamcity[testStarted  name='junit_framework.passedScenario']";
        String testFinishedExpectedMessage = "##teamcity[testFinished  duration='100' name='junit_framework.passedScenario']";

        String testStartedExpectedMessage2 = "##teamcity[testStarted  name='junit_framework.passedScenario2']";
        String testFinishedExpectedMessage2 = "##teamcity[testFinished  duration='100' name='junit_framework.passedScenario2']";

        teamCityStepListener.testSuiteStarted(TestCase.class);
        teamCityStepListener.testFinished(testOutcome);
        teamCityStepListener.testSuiteStarted(TestCase.class);
        teamCityStepListener.testFinished(testOutcome2);
        teamCityStepListener.testSuiteFinished();

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(logger, times(6)).info(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getAllValues().get(0), is(testSuiteStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(1), is(testStartedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(2), is(testFinishedExpectedMessage));
        assertThat(stringArgumentCaptor.getAllValues().get(3), is(testStartedExpectedMessage2));
        assertThat(stringArgumentCaptor.getAllValues().get(4), is(testFinishedExpectedMessage2));
        assertThat(stringArgumentCaptor.getAllValues().get(5), is(testSuiteFinishedExpectedMessage));
    }
}
