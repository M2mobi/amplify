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
package com.github.stkent.amplify.utils.time;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

public final class SystemTimeUtil {

    @Nullable
    private static ISystemTimeProvider sharedInstance;

    public static long currentTimeMillis() {
        synchronized (SystemTimeUtil.class) {
            if (sharedInstance == null) {
                sharedInstance = new RealSystemTimeProvider();
            }
        }

        return sharedInstance.currentTimeMillis();
    }

    @VisibleForTesting
    public static void setSharedInstance(@NonNull final ISystemTimeProvider systemTimeProvider) {
        sharedInstance = systemTimeProvider;
    }

    private SystemTimeUtil() {

    }

}
