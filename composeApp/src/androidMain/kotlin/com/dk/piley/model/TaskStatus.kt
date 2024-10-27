import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.dk.piley.R
import com.dk.piley.model.task.RecurringTimeRange

/**
 * Convert recurring time range enum to a string
 *
 * @return string representing the time range, e.g. "Weekly" for WEEKLY
 */
@Composable
fun RecurringTimeRange.toText(): String {
    val timeRanges = stringArrayResource(id = R.array.time_range)
    return when (this) {
        RecurringTimeRange.DAILY -> timeRanges[0]
        RecurringTimeRange.WEEKLY -> timeRanges[1]
        RecurringTimeRange.MONTHLY -> timeRanges[2]
        RecurringTimeRange.YEARLY -> timeRanges[3]
    }
}

/**
 * Convert time range string to recurring time range
 *
 * @param context generic context to fetch string resource
 * @return time range enum representing corresponding string
 * e.g. WEEKLY for "Weekly"
 */
fun String.toRecurringTimeRange(context: Context): RecurringTimeRange {
    val timeRanges = context.resources.getStringArray(R.array.time_range)
    return when (this) {
        timeRanges[0] -> RecurringTimeRange.DAILY
        timeRanges[1] -> RecurringTimeRange.WEEKLY
        timeRanges[2] -> RecurringTimeRange.MONTHLY
        timeRanges[3] -> RecurringTimeRange.YEARLY
        else -> RecurringTimeRange.DAILY
    }
}