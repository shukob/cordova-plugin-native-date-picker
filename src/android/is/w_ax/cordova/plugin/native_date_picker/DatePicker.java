/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package is.w_ax.cordova.plugin.native_date_picker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.apache.cordova.BuildHelper;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * This class launches the camera view, allows the user to take a picture, closes the camera view,
 * and returns the captured image.  When the camera view is closed, the screen displayed before
 * the camera view was shown is redisplayed.
 */
public class DatePicker extends CordovaPlugin {

    public CallbackContext callbackContext;
    public String applicationId;
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArry of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return A PluginResult object with a status and message.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        //Adding an API to CoreAndroid to get the BuildConfigValue
        //This allows us to not make this a breaking change to embedding
        this.applicationId = (String) BuildHelper.getBuildConfigValue(cordova.getActivity(), "APPLICATION_ID");
        this.applicationId = preferences.getString("applicationId", this.applicationId);


        if (action.equals("show")) {
            try {
                Activity activity = cordova.getActivity();
                Date date = new Date();
                if (args.length() > 0) {
                    JSONObject obj = args.getJSONObject(0);
                    if (obj.has("date")) {
                        String dateString = obj.getString("date");
                        date = simpleDateFormat.parse(dateString);
                    }
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int spinnerTheme = getAppResource("NumberPickerStyle", "style");
                int dialogTheme = getAppResource("DialogStyle", "style");
                DatePickerDialog dialog = new SpinnerDatePickerDialogBuilder()
                        .context(activity)
                        .callback(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                try {
                                    JSONObject resultJson = new JSONObject();
                                    resultJson.put("year", year);
                                    resultJson.put("monthOfYear", monthOfYear);
                                    resultJson.put("dayOfMonth", dayOfMonth);
                                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, resultJson);
                                    DatePicker.this.callbackContext.sendPluginResult(pluginResult);
                                } catch (JSONException e) {
                                    DatePicker.this.callbackContext.error(e.getLocalizedMessage());
                                }

                            }
                        })
                        .spinnerTheme(spinnerTheme)
                        .dialogTheme(dialogTheme)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .defaultDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .maxDate(2030, 0, 1)
                        .minDate(1920, 0, 1)
                        .build();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                        DatePicker.this.callbackContext.sendPluginResult(pluginResult);
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                        DatePicker.this.callbackContext.sendPluginResult(pluginResult);
                    }
                });



                dialog.show();
            } catch (Exception e) {
                callbackContext.error("Failed to get activity");
            }


            return true;
        }
        return false;
    }

    private int getAppResource(String name, String type) {
        return cordova.getActivity().getResources().getIdentifier(name, type, cordova.getActivity().getPackageName());
    }
}