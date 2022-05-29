package com.interview.pagination.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "users")
data class Results(
    @SerializedName("gender")
    val gender: String? = "",
    @SerializedName("name")
    @Expose
    val name: Name,
    @SerializedName("location")
    @Expose
    val location: Location,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    @Expose
    val id: Id,
    @SerializedName("picture")
    @Expose
    val picture: Picture
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var serialNumber: Int = 0
}

data class Coordinates(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
) : Serializable

data class Id(
    @SerializedName("name")
    val name: String,
    @SerializedName("value")
    val value: String? = ""
) : Serializable

data class Info(
    @SerializedName("seed")
    val seed: String,
    @SerializedName("results")
    val results: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("version")
    val version: Double
) : Serializable

data class Location(
    @SerializedName("street")
    val street: Street,
    @SerializedName("city")
    val city: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("postcode")
    val postcode: String,
    @SerializedName("coordinates")
    val coordinates: Coordinates,
    @SerializedName("timezone")
    val timezone: Timezone
) : Serializable

@Entity(tableName = "userNames")
data class Name(
    @SerializedName("title")
    val title: String,
    @SerializedName("first")
    val first: String,
    @SerializedName("last")
    val last: String
) : Serializable

data class Picture(
    @SerializedName("large")
    val large: String,
    @SerializedName("medium")
    val medium: String,
    @SerializedName("thumbnail")
    val thumbnail: String
) : Serializable

data class Street(
    @SerializedName("number")
    val number: Int,
    @SerializedName("name")
    val name: String
) : Serializable


data class Timezone(
    @SerializedName("offset")
    val offset: String,
    @SerializedName("description")
    val description: String
) : Serializable

data class UsersResponse(
    @SerializedName("results")
    val results: List<Results>,
    @SerializedName("info")
    val info: Info
) : Serializable