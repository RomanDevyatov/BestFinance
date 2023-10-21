package com.romandevyatov.bestfinance.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.EditText
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils {

    companion object {

        fun setupDatePicker(
            editText: EditText,
            dateFormat: SimpleDateFormat,
            initialDate: Calendar = Calendar.getInstance()
        ) {
            val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                initialDate.set(Calendar.YEAR, year)
                initialDate.set(Calendar.MONTH, month)
                initialDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                editText.setText(dateFormat.format(initialDate.time))
            }

            editText.setOnClickListener {
                DatePickerDialog(
                    editText.context,
                    datePickerListener,
                    initialDate.get(Calendar.YEAR),
                    initialDate.get(Calendar.MONTH),
                    initialDate.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            editText.setText(dateFormat.format(initialDate.time))
        }

        fun setupTimePicker(
            editText: EditText,
            timeFormat: SimpleDateFormat,
            initialDate: Calendar = Calendar.getInstance()
        ) {

            val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                initialDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                initialDate.set(Calendar.MINUTE, minute)
                editText.setText(LocalDateTimeRoomTypeConverter.timeFormat.format(initialDate.time))
            }

            editText.setOnClickListener {
                TimePickerDialog(
                    editText.context,
                    timePickerListener,
                    initialDate.get(Calendar.HOUR_OF_DAY),
                    initialDate.get(Calendar.MINUTE),
                    false
                ).show()
            }

            editText.setText(timeFormat.format(initialDate.time))
        }
    }
}