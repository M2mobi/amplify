/*
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
package com.github.stkent.amplify.feedback;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.github.stkent.amplify.IApp;
import com.github.stkent.amplify.IDevice;
import com.github.stkent.amplify.IEnvironment;

public interface IFeedbackCollector {

    /**
     * @return true to indicate that feedback collection has been started successfully; false otherwise.
     */
    boolean tryCollectingFeedback(
            @NonNull Activity currentActivity,
            @NonNull IApp app,
            @NonNull IEnvironment environment,
            @NonNull IDevice device);

}
