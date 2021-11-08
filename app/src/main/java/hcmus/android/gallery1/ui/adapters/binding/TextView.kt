package hcmus.android.gallery1.ui.adapters.binding

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.text.format.Formatter
import android.widget.TextView
import androidx.databinding.BindingAdapter

@SuppressLint("SetTextI18n")
@BindingAdapter("dateModifiedFromMediaStore")
fun TextView.dateModifiedFromMediaStore(seconds: Long?) {
    seconds?.let {
        val dateFormat = DateFormat.getLongDateFormat(context)
        val timeFormat = DateFormat.getTimeFormat(context)
        val timeStamp = seconds * 1000L // seconds to millis
        text = "${dateFormat.format(timeStamp)} ${timeFormat.format(timeStamp)}"
    }
}

@BindingAdapter("byteCountToDisplaySize")
fun TextView.byteCountToDisplaySize(byteCount: Long?) {
    byteCount?.let {
        text = Formatter.formatFileSize(context, byteCount)
    }
}