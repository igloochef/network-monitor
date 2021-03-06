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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ca.rmen.android.networkmonitor.Constants;
import ca.rmen.android.networkmonitor.R;
import ca.rmen.android.networkmonitor.app.prefs.SelectFieldsFragment.SelectFieldsFragmentListener;
import ca.rmen.android.networkmonitor.provider.NetMonColumns;
import ca.rmen.android.networkmonitor.util.Log;

public class SelectFieldsActivity extends AppCompatActivity implements SelectFieldsFragmentListener { // NO_UCD (use default)
    private static final String TAG = Constants.TAG + SelectFieldsActivity.class.getSimpleName();
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_fields);
        ListFragment lvf = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);
        mListView = lvf.getListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_fields, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        View okButton = findViewById(R.id.ok);
        switch (item.getItemId()) {
            case R.id.action_select_all:
                for (int i = 0; i < mListView.getCount(); i++)
                    mListView.setItemChecked(i, true);
                okButton.setEnabled(true);
                break;
            case R.id.action_select_none:
                for (int i = 0; i < mListView.getCount(); i++)
                    mListView.setItemChecked(i, false);
                okButton.setEnabled(false);
                break;
            case R.id.action_select_profile_wifi:
                selectColumns(getResources().getStringArray(R.array.db_columns_profile_wifi));
                okButton.setEnabled(true);
                break;
            case R.id.action_select_profile_mobile_gsm:
                selectColumns(getResources().getStringArray(R.array.db_columns_profile_mobile_gsm));
                okButton.setEnabled(true);
                break;
            case R.id.action_select_profile_mobile_cdma:
                selectColumns(getResources().getStringArray(R.array.db_columns_profile_mobile_cdma));
                okButton.setEnabled(true);
                break;
            case R.id.action_select_profile_location:
                selectColumns(getResources().getStringArray(R.array.db_columns_profile_location));
                okButton.setEnabled(true);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void selectColumns(String[] columnNames) {
        for (int i = 0; i < mListView.getCount(); i++)
            mListView.setItemChecked(i, false);
        @SuppressWarnings("unchecked")
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) mListView.getAdapter();
        for (String columnName : columnNames) {
            String columnLabel = NetMonColumns.getColumnLabel(this, columnName);
            int position = adapter.getPosition(columnLabel);
            mListView.setItemChecked(position, true);
        }
    }

    public void onCancel(@SuppressWarnings("UnusedParameters") View v) {
        Log.v(TAG, "onCancel");
        finish();
    }

    public void onOk(@SuppressWarnings("UnusedParameters") View v) {
        Log.v(TAG, "onOk");
        SparseBooleanArray checkedPositions = mListView.getCheckedItemPositions();
        String[] dbColumns = NetMonColumns.getColumnNames(this);
        final List<String> selectedColumns = new ArrayList<>(dbColumns.length);
        for (int i = 0; i < dbColumns.length; i++) {
            if (checkedPositions.get(i)) selectedColumns.add(dbColumns[i]);
        }
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                NetMonPreferences.getInstance(SelectFieldsActivity.this).setSelectedColumns(selectedColumns);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        }.execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.v(TAG, "onListItemClick: clicked on view " + v + " at position " + position + " with id " + id);
        View okButton = findViewById(R.id.ok);
        SparseBooleanArray checkedItemPositions = l.getCheckedItemPositions();
        for (int i = 0; i < checkedItemPositions.size(); i++) {
            if (checkedItemPositions.get(checkedItemPositions.keyAt(i))) {
                okButton.setEnabled(true);
                return;
            }
        }
        okButton.setEnabled(false);
    }
}
