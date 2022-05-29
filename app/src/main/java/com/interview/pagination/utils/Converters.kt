package com.interview.pagination.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.interview.pagination.model.Id
import com.interview.pagination.model.Location
import com.interview.pagination.model.Name
import com.interview.pagination.model.Picture

class Converters {
    @TypeConverter
    fun locationToString(app: Location): String = Gson().toJson(app)

    @TypeConverter
    fun stringToLocation(string: String): Location = Gson().fromJson(string, Location::class.java)

    @TypeConverter
    fun pictureToString(app: Picture): String = Gson().toJson(app)

    @TypeConverter
    fun stringToPicture(string: String): Picture = Gson().fromJson(string, Picture::class.java)

    @TypeConverter
    fun idToString(app: Id): String = Gson().toJson(app)

    @TypeConverter
    fun stringToId(string: String): Id = Gson().fromJson(string, Id::class.java)

    @TypeConverter
    fun nameToString(app: Name): String = Gson().toJson(app)

    @TypeConverter
    fun stringToName(string: String): Name = Gson().fromJson(string, Name::class.java)
}