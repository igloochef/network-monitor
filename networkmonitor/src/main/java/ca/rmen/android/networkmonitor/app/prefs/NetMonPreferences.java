/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2014-2015 Carmen Alvarez (c@rmen.ca)
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
package ca.rmen.android.networkmonitor.app.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.rmen.android.networkmonitor.R;
import ca.rmen.android.networkmonitor.app.prefs.SortPreferences.SortOrder;
import ca.rmen.android.networkmonitor.app.service.scheduler.AlarmManagerScheduler;
import ca.rmen.android.networkmonitor.app.service.scheduler.ExecutorServiceScheduler;
import ca.rmen.android.networkmonitor.app.service.scheduler.NetworkChangeScheduler;
import ca.rmen.android.networkmonitor.app.service.scheduler.Scheduler;
import ca.rmen.android.networkmonitor.provider.NetMonColumns;

/**
 * Convenience methods for getting/setting shared preferences.
 */
public class NetMonPreferences {

    public enum CellIdFormat {
        DECIMAL, HEX, DECIMAL_HEX
    }

    public enum LocationFetchingStrategy {
        SAVE_POWER, HIGH_ACCURACY
    }


    static final String PREF_TEST_SERVER = "PREF_TEST_SERVER";
    public static final int PREF_MIN_POLLING_INTERVAL = 10000;
    public static final String PREF_UPDATE_INTERVAL = "PREF_UPDATE_INTERVAL";
    public static final int PREF_UPDATE_ON_NETWORK_CHANGE = -1;
    public static final String PREF_SERVICE_ENABLED = "PREF_SERVICE_ENABLED";
    public static final boolean PREF_SERVICE_ENABLED_DEFAULT = false;
    public static final String PREF_SCHEDULER = "PREF_SCHEDULER";
    public static final String PREF_SORT_ORDER = "PREF_SORT_ORDER";
    public static final String PREF_SORT_COLUMN_NAME = "PREF_SORT_COLUMN_NAME";

    public static final String PREF_KML_EXPORT_COLUMN = "PREF_KML_EXPORT_COLUMN";
    public static final String PREF_FILTER_RECORD_COUNT = "PREF_FILTER_RECORD_COUNT";
    public static final String PREF_FILTER_RECORD_COUNT_DEFAULT = "100";
    public static final String PREF_DB_RECORD_COUNT = "PREF_DB_RECORD_COUNT";
    public static final String PREF_ENABLE_CONNECTION_TEST = "PREF_ENABLE_CONNECTION_TEST";
    public static final String PREF_CELL_ID_FORMAT = "PREF_CELL_ID_FORMAT";
    public static final String PREF_CELL_ID_FORMAT_DEFAULT = "decimal";
    public static final String PREF_LOCATION_FETCHING_STRATEGY = "PREF_LOCATION_FETCHING_STRATEGY";
    public static final String PREF_NOTIFICATION_RINGTONE = "PREF_NOTIFICATION_RINGTONE";
    public static final String PREF_NOTIFICATION_ENABLED = "PREF_NOTIFICATION_ENABLED";

    private static final String PREF_WAKE_INTERVAL = "PREF_WAKE_INTERVAL";
    private static final String PREF_UPDATE_INTERVAL_DEFAULT = "10000";
    private static final String PREF_DB_RECORD_COUNT_DEFAULT = "-1";
    private static final boolean PREF_ENABLE_CONNECTION_TEST_DEFAULT = true;

    private static final String PREF_TEST_SERVER_DEFAULT = "173.194.45.41";
    private static final String PREF_WAKE_INTERVAL_DEFAULT = "0";
    private static final String PREF_SCHEDULER_DEFAULT = ExecutorServiceScheduler.class.getSimpleName();
    private static final String PREF_SELECTED_COLUMNS = "PREF_SELECTED_COLUMNS";
    private static final String PREF_SORT_COLUMN_NAME_DEFAULT = NetMonColumns.TIMESTAMP;
    private static final String PREF_SORT_ORDER_DEFAULT = SortOrder.DESC.name();
    private static final String PREF_FILTER_PREFIX = "PREF_FILTERED_VALUES_";

    private static final String PREF_EXPORT_FOLDER = "PREF_EXPORT_FOLDER";
    private static final String PREF_IMPORT_FOLDER = "PREF_IMPORT_FOLDER";

    private static NetMonPreferences INSTANCE = null;
    private final SharedPreferences mSharedPrefs;
    private final Context mContext;

    public static synchronized NetMonPreferences getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NetMonPreferences(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private NetMonPreferences(Context context) {
        mContext = context;
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return The server we try to reach to test connectivity.
     */
    public String getTestServer() {
        return mSharedPrefs.getString(NetMonPreferences.PREF_TEST_SERVER, NetMonPreferences.PREF_TEST_SERVER_DEFAULT);
    }

    /**
     * @return the interval between log entries, in millis
     */
    public int getUpdateInterval() {
        return getIntPreference(NetMonPreferences.PREF_UPDATE_INTERVAL, NetMonPreferences.PREF_UPDATE_INTERVAL_DEFAULT);
    }

    /**
     * @return true if we're polling quickly
     */
    public boolean isFastPollingEnabled() {
        int updateInterval = getUpdateInterval();
        return updateInterval > 0 && updateInterval < NetMonPreferences.PREF_MIN_POLLING_INTERVAL;
    }

    /**
     * @return the interval, in milliseconds, between forced waking up the device (by turning the screen on). If not positive, we will never force wake up the
     *         device.
     */
    public int getWakeInterval() {
        return getIntPreference(NetMonPreferences.PREF_WAKE_INTERVAL, NetMonPreferences.PREF_WAKE_INTERVAL_DEFAULT);
    }

    /**
     * @return true if we are currently collecting and logging data.
     */
    public boolean isServiceEnabled() {
        return mSharedPrefs.getBoolean(NetMonPreferences.PREF_SERVICE_ENABLED, NetMonPreferences.PREF_SERVICE_ENABLED_DEFAULT);
    }

    /**
     * @param value true if we should collect and log data, false otherwise.
     */
    public void setServiceEnabled(boolean value) {
        setBooleanPreference(NetMonPreferences.PREF_SERVICE_ENABLED, value);
    }

    /**
     * @return the number of rows we should display in the log view. This is only for display: we always export all rows.
     */
    public int getFilterRecordCount() {
        return getIntPreference(NetMonPreferences.PREF_FILTER_RECORD_COUNT, NetMonPreferences.PREF_FILTER_RECORD_COUNT_DEFAULT);
    }

    /**
     * @return the number of rows we should store in the database
     */
    public int getDBRecordCount() {
        return getIntPreference(NetMonPreferences.PREF_DB_RECORD_COUNT, NetMonPreferences.PREF_DB_RECORD_COUNT_DEFAULT);
    }

    public void setDBRecordCount(int recordCount) {
        setIntPreference(NetMonPreferences.PREF_DB_RECORD_COUNT, recordCount);
    }

    /**
     * @return true if we should do connection tests with each test.
     */
    public boolean isConnectionTestEnabled() {
        return mSharedPrefs.getBoolean(NetMonPreferences.PREF_ENABLE_CONNECTION_TEST, NetMonPreferences.PREF_ENABLE_CONNECTION_TEST_DEFAULT);
    }

    /**
     * Enable or disable the data connection tests.
     */
    public void setConnectionTestEnabled(boolean enabled) {
        setBooleanPreference(NetMonPreferences.PREF_ENABLE_CONNECTION_TEST, enabled);
    }

    /**
     * @return the format in which numeric cell id fields should be displayed and exported.
     */
    public CellIdFormat getCellIdFormat() {
        String cellIdFormat = mSharedPrefs.getString(NetMonPreferences.PREF_CELL_ID_FORMAT, NetMonPreferences.PREF_CELL_ID_FORMAT_DEFAULT);
        if ("decimal".equals(cellIdFormat)) return CellIdFormat.DECIMAL;
        if ("hex".equals(cellIdFormat)) return CellIdFormat.HEX;
        return CellIdFormat.DECIMAL_HEX;
    }

    /**
     * @return the implementation of the {@link Scheduler} interface which schedules each logging of data.
     */
    public Class<?> getSchedulerClass() {
        int updateInterval = getUpdateInterval();
        if (updateInterval == PREF_UPDATE_ON_NETWORK_CHANGE) return NetworkChangeScheduler.class;
        String schedulerPref = mSharedPrefs.getString(NetMonPreferences.PREF_SCHEDULER, NetMonPreferences.PREF_SCHEDULER_DEFAULT);
        if (schedulerPref.equals(AlarmManagerScheduler.class.getSimpleName())) return AlarmManagerScheduler.class;
        else
            return ExecutorServiceScheduler.class;
    }

    /**
     * @return the list of columns which will appear in the log view. This is only for display. All columns will be exported.
     */
    public List<String> getSelectedColumns() {
        String selectedColumnsString = mSharedPrefs.getString(NetMonPreferences.PREF_SELECTED_COLUMNS, null);
        final String[] selectedColumns;
        if (TextUtils.isEmpty(selectedColumnsString)) selectedColumns = NetMonColumns.getDefaultVisibleColumnNames(mContext);
        else
            selectedColumns = selectedColumnsString.split(",");
        return Arrays.asList(selectedColumns);
    }

    /**
     * set the list of columns to appear in the log view. This is only for display. All columns will be exported.
     */
    public void setSelectedColumns(List<String> selectedColumns) {
        String selectedColumnsString = TextUtils.join(",", selectedColumns);
        setStringPreference(NetMonPreferences.PREF_SELECTED_COLUMNS, selectedColumnsString);
    }

    /**
     * The report will only show rows where the given column has one of the given values.
     */
    void setColumnFilterValues(String columnName, List<String> filteredValues) {
        String filteredValuesString = TextUtils.join(",", filteredValues);
        setStringPreference(NetMonPreferences.PREF_FILTER_PREFIX + columnName, filteredValuesString);
    }

    /**
     * Get the list of values for this column on which we'll filter the report.
     */
    public List<String> getColumnFilterValues(String columnName) {
        String filteredValuesString = mSharedPrefs.getString(PREF_FILTER_PREFIX + columnName, "");
        if (TextUtils.isEmpty(filteredValuesString)) return new ArrayList<>();
        return Arrays.asList(filteredValuesString.split(","));
    }

    /**
     * Clear any filtering preferences the user has set.
     */
    public void resetColumnFilters() {
        String[] filterableColumns = mContext.getResources().getStringArray(R.array.filterable_columns);
        Editor editor = mSharedPrefs.edit();
        for (String filterableColumn : filterableColumns)
            editor.putString(PREF_FILTER_PREFIX + filterableColumn, null);
        editor.apply();
    }

    /**
     * @return true if the user has chosen to filter at least one column.
     */
    public boolean hasColumnFilters() {
        String[] filterableColumns = mContext.getResources().getStringArray(R.array.filterable_columns);
        for (String filterableColumn : filterableColumns)
            if (!TextUtils.isEmpty(mSharedPrefs.getString(PREF_FILTER_PREFIX + filterableColumn, null))) return true;
        return false;
    }

    /**
     * @return the settings for how rows in the log view or table export formats are sorted.
     */
    public SortPreferences getSortPreferences() {
        String sortColumnName = mSharedPrefs.getString(PREF_SORT_COLUMN_NAME, PREF_SORT_COLUMN_NAME_DEFAULT);
        SortOrder sortOrder = SortOrder.valueOf(mSharedPrefs.getString(PREF_SORT_ORDER, PREF_SORT_ORDER_DEFAULT));
        return new SortPreferences(sortColumnName, sortOrder);
    }

    /**
     * set the settings for how rows in the log view or table export formats should be sorted.
     */
    public void setSortPreferences(SortPreferences sortPreferences) {
        Editor editor = mSharedPrefs.edit();
        editor.putString(PREF_SORT_COLUMN_NAME, sortPreferences.sortColumnName);
        editor.putString(PREF_SORT_ORDER, sortPreferences.sortOrder.name());
        editor.apply();
    }

    /**
     * @return the strategy we should use for requesting location updates.
     */
    public LocationFetchingStrategy getLocationFetchingStrategy() {
        String value = mSharedPrefs.getString(PREF_LOCATION_FETCHING_STRATEGY, LocationFetchingStrategy.SAVE_POWER.name());
        return LocationFetchingStrategy.valueOf(value);
    }

    /**
     * @return true if we should display a warning notification when a network test fails.
     */
    public boolean getShowNotificationOnTestFailure() {
        return mSharedPrefs.getBoolean(PREF_NOTIFICATION_ENABLED, false);
    }

    /**
     * @return the Uri of the sound to play when a notification is created.
     */
    public Uri getNotificationSoundUri() {
        String soundUriStr = mSharedPrefs.getString(PREF_NOTIFICATION_RINGTONE, null);
        if (TextUtils.isEmpty(soundUriStr)) return null;
        return Uri.parse(soundUriStr);
    }

    /**
     * Set the ringtone Uri to the default ringtone Uri.
     */
    public void setDefaultNotificationSoundUri() {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (defaultSoundUri != null) setStringPreference(PREF_NOTIFICATION_RINGTONE, defaultSoundUri.toString());
    }

    public File getExportFolder() {
        String folder = mSharedPrefs.getString(PREF_EXPORT_FOLDER, null);
        if(folder != null) return new File(folder);
        return Environment.getExternalStorageDirectory();
    }

    public void setExportFolder(File folder) {
        setStringPreference(PREF_EXPORT_FOLDER, folder.getAbsolutePath());
    }

    public File getImportFolder() {
        String folder = mSharedPrefs.getString(PREF_IMPORT_FOLDER, null);
        if(folder != null) return new File(folder);
        return Environment.getExternalStorageDirectory();
    }

    public void setImportFolder(File folder) {
        setStringPreference(PREF_IMPORT_FOLDER, folder.getAbsolutePath());
    }

    private int getIntPreference(String key, String defaultValue) {
        String valueStr = mSharedPrefs.getString(key, defaultValue);
        return Integer.valueOf(valueStr);
    }

    private void setIntPreference(String key, int value) {
        setStringPreference(key, String.valueOf(value));
    }

    private void setStringPreference(final String key, final String value) {
        mSharedPrefs.edit().putString(key, value).apply();
    }

    private void setBooleanPreference(final String key, final Boolean value) {
        mSharedPrefs.edit().putBoolean(key, value).apply();
    }

}
