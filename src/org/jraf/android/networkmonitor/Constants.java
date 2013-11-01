/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2013 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * Copyright (C) 2013 Carmen Alvarez (c@rmen.ca)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jraf.android.networkmonitor;

public class Constants {
    public static final String TAG = "NetMon/";

    public static final String PREF_UPDATE_INTERVAL = "PREF_UPDATE_INTERVAL";
    public static final String PREF_UPDATE_INTERVAL_DEFAULT = "10000";
    public static final String PREF_WAKE_INTERVAL = "PREF_WAKE_INTERVAL";
    public static final String PREF_WAKE_INTERVAL_DEFAULT = "0";
    public static final String PREF_SERVICE_ENABLED = "PREF_SERVICE_ENABLED";
    public static final String PREF_RESET_LOG_FILE = "PREF_RESET_LOG_FILE";
    public static final boolean PREF_SERVICE_ENABLED_DEFAULT = false;
    public static final String CONNECTION_TEST_PASS = "PASS";
    public static final String CONNECTION_TEST_FAIL = "FAIL";
    public static final String CONNECTION_TEST_SLOW = "SLOW";
    public static final String DATA_STATE_CONNECTED = "CONNECTED";
    public static final String CONNECTION_TYPE_WIFI = "WIFI";
}
