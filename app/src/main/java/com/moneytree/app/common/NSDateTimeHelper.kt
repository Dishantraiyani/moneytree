package com.moneytree.app.common

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object NSDateTimeHelper {
    private val TAG = NSDateTimeHelper::class.java.simpleName
    private const val DATE_FORMAT_ORDER_SHOW = "dd-MM-yyyy | HH:mm a"
    private const val DATE_FORMAT_FROM_API_TRANSACTION = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val DATE_FORMAT_FROM_API = "yyyy-MM-dd'T'HH:mm:ss"
    private const val DATE_FORMAT_CURRENT_TIME = "dd-MM-yyyy"
    private const val DATE_FORMAT_USER_ = "dd MMM, HH:mma"
    private const val DATE_FORMAT_GROUP_DATE = "yyyy-MM-dd"
    private const val DATE_FORMAT_GROUP_TIME = "HH:mm a"
    private const val DATE_OF_TIME = "HH:mm a"
    private const val DATE_FOR_NOTIFICATION = "yyyy-MM-dd"

    /**
     * To convert the input date string to expected output pattern
     *
     * @param dateString date that need to convert to output pattern
     * @param inputPattern pattern of input date string
     * @param outputPattern output pattern of date string
     * @return date string
     */
    @Suppress("SameParameterValue")
    private fun getConvertedDate(
        dateString: String?, inputPattern: String, outputPattern: String): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat(inputPattern, Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date?
        try {
            date = dateString?.let { format.parse(it) }
            date?.let {
                calendar.time = it
            }
        } catch (exception: ParseException) {
            NSLog.e(TAG, "getConvertedDate : Caught Exception ", exception)
        }
        val newFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return newFormat.format(calendar.time)
    }

    /**
     * To convert the input date string to expected output pattern
     *
     * @param dateString date that need to convert to output pattern
     * @return date string
     */
    fun getDateValue(
        dateString: String?): Date? {
        val format = SimpleDateFormat(DATE_FORMAT_FROM_API, Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date? = try {
            dateString?.let { format.parse(it) }
        } catch (exception: ParseException) {
            null
        }
        return date
    }

    /**
     * To get the transaction time string for view
     *
     * @param dateString The date string
     */
    fun getDateTimeForView(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API_TRANSACTION, DATE_FORMAT_ORDER_SHOW)

    /**
     * To get the current dateTime string
     *
     */
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT_CURRENT_TIME, Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * To get the current dateTime string
     *
     */
    fun getCurrentDateNotification(): String {
        val sdf = SimpleDateFormat(DATE_FOR_NOTIFICATION, Locale.getDefault())
        return sdf.format(Date())
    }


    /**
     * To get the current dateTime string
     *
     */
    fun getCurrentDatePayment(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT_USER_, Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * To get the transaction time string for view
     *
     * @param dateString The date string
     */
    fun getDateTimeForGroupUser(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API_TRANSACTION, DATE_FORMAT_USER_)

    /**
     * To get the date string for view
     *
     * @param dateString The date string
     */
    fun getDateForGroup(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API_TRANSACTION, DATE_FORMAT_GROUP_DATE)

    /**
     * To get the time string for view
     *
     * @param dateString The date string
     */
    fun getTimeForGroup(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API_TRANSACTION, DATE_FORMAT_GROUP_TIME)

    /**
     * To get the Notification time string for view
     *
     * @param dateString The date string
     */
    fun getDateOfTime(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API, DATE_OF_TIME)

    /**
     * To get the Notification time string for view
     *
     * @param dateString The date string
     */
    fun getNotificationFilterDate(dateString: String?) =
        getConvertedDate(dateString, DATE_FORMAT_FROM_API, DATE_FOR_NOTIFICATION)

    fun getConvertedDate(date: Date = getNextDate(increaseDecrease = 1)): String {
        val sdf = SimpleDateFormat(DATE_FOR_NOTIFICATION, Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * To get the previous dateTime string
     *
     */
    private fun getNextDate(date: Date = Date(), increaseDecrease: Int): Date {
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, increaseDecrease)
        return c.time
    }
}
