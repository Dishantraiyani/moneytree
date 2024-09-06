package com.moneytree.app.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of wallet list
 */
data class MeetingsDataResponse(
    @field:SerializedName("venue")
    val venue: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("claim_amount")
    val claimAmount: Any? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("created_at")
    val createdAt: Any? = null,

    @field:SerializedName("meeting_review")
    val meetingReview: String? = null,

    @field:SerializedName("old_person")
    val oldPerson: String? = null,

    @field:SerializedName("is_delete")
    val isDelete: String? = null,

    @field:SerializedName("event_by")
    val eventBy: String? = null,

    @field:SerializedName("created_id")
    val createdId: Any? = null,

    @field:SerializedName("event_id")
    val eventId: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: Any? = null,

    @field:SerializedName("meeting_result")
    val meetingResult: String? = null,

    @field:SerializedName("event_date")
    val eventDate: String? = null,

    @field:SerializedName("event_name")
    val eventName: String? = null,

    @field:SerializedName("new_person")
    val newPerson: String? = null,

    @field:SerializedName("meeting_image")
    val meetingImage: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("updated_id")
    val updatedId: Any? = null,

    @field:SerializedName("special_guest_name")
    val specialGuestName: String? = null,

    @field:SerializedName("host_name")
    val hostName: String? = null,

    @field:SerializedName("event_time")
    val eventTime: String? = null
)

data class MeetingsListResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean = false,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: MutableList<MeetingsDataResponse> = arrayListOf()
)

//{"status":true,"message":"success","data":[{"event_id":"65","event_name":"","host_name":"hostewhb1","event_date":"2024-09-17","event_time":"21:35:00","venue":"Gadhaka Rd","city":"Tramba, Rajkot","state":"Gujarat","is_delete":"N","created_id":null,"updated_id":null,"created_at":null,"updated_at":null,"event_by":"9010530525","mobile":"9784546312","special_guest_name":"spsgk1","new_person":"newbdkd","old_person":"shhzjs","meeting_result":"hxjxj","claim_amount":null,"meeting_review":"","meeting_image":"1725552284.jpg"}]}