package com.netcast.baidutv.ui.ui.settings

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment(private val listner:TimePickerDialog.OnTimeSetListener) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Use the current time as the default values for the picker
        val calendar: Calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)

        /** Create a new instance of TimePickerDialog and return it
         * && implement OnTimeSetListener on Activity.**/
        return TimePickerDialog(
            requireContext(),
            listner,
            hour,
            minute,
            false)
    }
}