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
package ca.rmen.android.networkmonitor.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import ca.rmen.android.networkmonitor.Constants;
import ca.rmen.android.networkmonitor.util.Log;

/**
 * Shows a dialog with a title, message, and a single button to dismiss the dialog.
 */
public class InfoDialogFragment extends DialogFragment { // NO_UCD (use default)

    private static final String TAG = Constants.TAG + InfoDialogFragment.class.getSimpleName();

    public interface InfoDialogListener {
        void onNeutralClicked(int actionId, Bundle extras);
    }

    public InfoDialogFragment() {
        super();
    }

    /**
     * @return a Dialog with a title, message, and single button to dismiss the dialog.
     */
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "onCreateDialog: savedInstanceState = " + savedInstanceState);
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Bundle arguments = getArguments();
        final int actionId = arguments.getInt(DialogFragmentFactory.EXTRA_ACTION_ID);
        final int iconId = arguments.getInt(DialogFragmentFactory.EXTRA_ICON_ID);
        if (iconId > 0) builder.setIcon(iconId);
        final Bundle extras = arguments.getBundle(DialogFragmentFactory.EXTRA_EXTRAS);
        builder.setTitle(arguments.getString(DialogFragmentFactory.EXTRA_TITLE)).setMessage(arguments.getString(DialogFragmentFactory.EXTRA_MESSAGE));
        if (getActivity() instanceof InfoDialogListener) {
            OnClickListener neutralListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FragmentActivity activity = getActivity();
                    if (activity == null) Log.w(TAG, "User clicked on dialog after it was detached from activity. Monkey?");
                    else
                        ((InfoDialogListener) activity).onNeutralClicked(actionId, extras);
                }
            };
            builder.setNeutralButton(android.R.string.ok, neutralListener);
        }
        if (getActivity() instanceof OnCancelListener) builder.setOnCancelListener((OnCancelListener) getActivity());
        final Dialog dialog = builder.create();
        if (getActivity() instanceof OnDismissListener) dialog.setOnDismissListener((OnDismissListener) getActivity());
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.v(TAG, "onDismiss");
        super.onDismiss(dialog);
        if (getActivity() instanceof OnDismissListener) ((OnDismissListener) getActivity()).onDismiss(dialog);
    }
}
