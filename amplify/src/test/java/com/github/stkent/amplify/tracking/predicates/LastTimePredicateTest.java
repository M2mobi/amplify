package com.github.stkent.amplify.tracking.predicates;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.github.stkent.amplify.helpers.BaseTest;
import com.github.stkent.amplify.helpers.MockSettings;
import com.github.stkent.amplify.helpers.StubbedLogger;
import com.github.stkent.amplify.tracking.ClockUtil;
import com.github.stkent.amplify.tracking.interfaces.IApplicationInfoProvider;
import com.github.stkent.amplify.tracking.interfaces.IEvent;
import com.github.stkent.amplify.tracking.interfaces.IEventCheck;

import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class LastTimePredicateTest extends BaseTest {

    private LastTimePredicate lastTimePredicate;

    private MockSettings<Long> mockSettings;

    @Mock
    private IApplicationInfoProvider mockApplicationInfoProvider;
    @Mock
    private IEvent mockEvent;
    @Mock
    private IEventCheck<Long> mockEventCheck;

    @Override
    public void localSetUp() {
        mockSettings = new MockSettings<>();

        lastTimePredicate = new LastTimePredicate(
                new StubbedLogger(),
                mockSettings,
                mockApplicationInfoProvider);

        lastTimePredicate.trackEvent(mockEvent, mockEventCheck);
    }

    @Test
    public void testThatFirstEventTimeIsRecorded() {
        // Arrange
        final long fakeEventTime = MARCH_18_2014_838PM_UTC;

        // Act
        triggerEventAtTime(mockEvent, fakeEventTime);

        // Assert

        // Check that the correct event time was saved:
        final Long savedEventTime = mockSettings.getEventValue(mockEvent, mockEventCheck);

        assertEquals("The correct time should have been recorded for this event", Long.valueOf(fakeEventTime), savedEventTime);
    }

    @SuppressLint("Assert")
    @Test
    public void testThatSecondEventTimesIsRecorded() {
        // Arrange Act
        final long fakeEventTimeEarlier = MARCH_18_2014_838PM_UTC;
        final long fakeEventTimeLater
                = fakeEventTimeEarlier + TimeUnit.DAYS.toMillis(1);

        assert fakeEventTimeEarlier < fakeEventTimeLater;

        // Act
        triggerEventAtTime(mockEvent, fakeEventTimeEarlier);
        triggerEventAtTime(mockEvent, fakeEventTimeLater);

        // Assert

        // Check that the earlier event time was saved, and the second event time ignored:
        final Long savedEventTime = mockSettings.getEventValue(mockEvent, mockEventCheck);

        assertEquals("The correct (latest) time should have been recorded for this event",
                Long.valueOf(fakeEventTimeLater), savedEventTime);
    }

    private void triggerEventAtTime(@NonNull final IEvent event, final long time) {
        ClockUtil.setFakeCurrentTimeMillis(time);
        lastTimePredicate.eventTriggered(event);
    }

}
