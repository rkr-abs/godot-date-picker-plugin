package com.oddsockgames.dateTimePickerPlugin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.ArraySet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.Calendar;
import java.util.Set;

public class DateTimePickerPlugin extends GodotPlugin {
    private final Godot context;
    private final Activity activity;
    private FrameLayout layout = null;

    public DateTimePickerPlugin(Godot godot) {
        super(godot);
        context = godot;
        activity = godot.getActivity();
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "DateTimePickerPlugin";
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();
        signals.add(new SignalInfo("onDatePicked", String.class, Dictionary.class));
        signals.add(new SignalInfo("onTimePicked", String.class, Dictionary.class));

        return signals;
    }



    @UsedByGodot
    public void showDatePicker() {
        showDatePicker("", new Dictionary());
    }

    @UsedByGodot
    public void showTimePicker() {showTimePicker("");}

    @UsedByGodot
    public void showTimePicker(String reference) {
        activity.runOnUiThread(() -> {

            final Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);

            Dictionary selectedTime = new Dictionary();

            TimePickerDialog timePicker = new TimePickerDialog(context.getActivity(),
                    (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute) -> {
                        selectedTime.put("hour", hourOfDay);
                        selectedTime.put("minute", minute);

                        emitSignal("onTimePicked", reference, selectedTime);
                    }, currentHour, currentMinute, false);

            timePicker.show();
        });
    }
    @UsedByGodot
    public void showDatePicker(String reference, Dictionary godotDictionary) {
        activity.runOnUiThread(() -> {
            int day = (int) godotDictionary.get("day");
            int month = (int) godotDictionary.get("month");
            int curYear = (int) godotDictionary.get("year");

            Dictionary selectedDate = new Dictionary();

            DatePickerDialog datePicker = new DatePickerDialog(context.getActivity(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        selectedDate.put("day", dayOfMonth);
                        selectedDate.put("month", (monthOfYear + 1));
                        selectedDate.put("year", year);

                        emitSignal("onDatePicked", reference, selectedDate);
                    }, curYear, month - 1, day);

            datePicker.show();
        });
    }
}
