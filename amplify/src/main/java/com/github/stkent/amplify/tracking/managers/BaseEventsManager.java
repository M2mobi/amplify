/**
 * Copyright 2015 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.stkent.amplify.tracking.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.stkent.amplify.ILogger;
import com.github.stkent.amplify.tracking.interfaces.IEvent;
import com.github.stkent.amplify.tracking.interfaces.IEventBasedRule;
import com.github.stkent.amplify.tracking.interfaces.IEventsManager;
import com.github.stkent.amplify.tracking.interfaces.ISettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseEventsManager<T> implements IEventsManager<T> {

    private static final String AMPLIFY_TRACKING_KEY_PREFIX = "AMPLIFY_";

    private final ILogger logger;
    private final ISettings<T> settings;
    private final ConcurrentHashMap<IEvent, List<IEventBasedRule<T>>> internalMap;

    /**
     * @return a key that uniquely identifies this event tracker within the embedding application
     */
    @NonNull
    protected abstract String getTrackingKeySuffix();

    /**
     * @param cachedEventValue the existing cached value associated with the tracked event; null if
     *                         the event has never occurred before
     * @return a new value to replace the existing value in the cache
     */
    @NonNull
    protected abstract T getUpdatedTrackingValue(@Nullable final T cachedEventValue);

    protected BaseEventsManager(
            @NonNull final ILogger logger,
            @NonNull final ISettings<T> settings) {
        this.logger = logger;
        this.settings = settings;
        this.internalMap = new ConcurrentHashMap<>();
    }

    @Override
    public void addEventBasedRule(
            @NonNull final IEvent event,
            @NonNull final IEventBasedRule<T> rule) {

        if (!isTrackingEvent(event)) {
            internalMap.put(event, new ArrayList<IEventBasedRule<T>>());
        }

        internalMap.get(event).add(rule);

        logger.d("Registered " + rule.getDescription() + " for event " + event.getTrackingKey());
    }

    @Override
    public void notifyEventTriggered(@NonNull final IEvent event) {
        if (isTrackingEvent(event)) {
            final T cachedTrackingValue = getCachedTrackingValue(event);
            final T updatedTrackingValue = getUpdatedTrackingValue(cachedTrackingValue);

            if (cachedTrackingValue == null) {
                logger.d(IEventBasedRule.class.getSimpleName()
                        + " setting event value to: "
                        + updatedTrackingValue);
            } else if (!updatedTrackingValue.equals(cachedTrackingValue)) {
                logger.d(IEventBasedRule.class.getSimpleName()
                        + " updating event value from: "
                        + cachedTrackingValue
                        + " to "
                        + updatedTrackingValue);
            }

            settings.writeTrackingValue(getTrackingKey(event), updatedTrackingValue);
        }
    }

    @Override
    public boolean shouldAllowFeedbackPrompt() {
        for (final Map.Entry<IEvent, List<IEventBasedRule<T>>> eventBasedRules : internalMap.entrySet()) {
            final IEvent event = eventBasedRules.getKey();

            for (final IEventBasedRule<T> eventBasedRule : eventBasedRules.getValue()) {
                final T cachedEventValue = getCachedTrackingValue(event);

                if (cachedEventValue != null) {
                    logger.d(getTrackingKey(event) + ": " + eventBasedRule.getStatusString(cachedEventValue));

                    if (!eventBasedRule.shouldAllowFeedbackPrompt(cachedEventValue)) {
                        logger.d("Blocking feedback for event: " + event + " because of rule: " + eventBasedRule);
                        return false;
                    }
                } else {
                    logger.d(getTrackingKey(event) + " has never occurred before!");

                    if (!eventBasedRule.shouldAllowFeedbackPromptByDefault()) {
                        logger.d("Blocking feedback for event: " + event + " because of rule: " + eventBasedRule);
                        return false;
                    }
                }


            }
        }

        return true;
    }

    private boolean isTrackingEvent(@NonNull final IEvent event) {
        return internalMap.containsKey(event);
    }

    private String getTrackingKey(@NonNull final IEvent event) {
        return AMPLIFY_TRACKING_KEY_PREFIX
                + event.getTrackingKey()
                + "_"
                + this.getTrackingKeySuffix().toUpperCase();
    }

    @Nullable
    private T getCachedTrackingValue(@NonNull final IEvent event) {
        return settings.readTrackingValue(getTrackingKey(event));
    }

}
