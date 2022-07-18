package com.team.alex.talent.data.dto

import android.os.Parcel
import android.os.Parcelable
import com.team.alex.talent.domain.model.Tag

data class TagDto(
    val color: String,
    val id: String,
    val name: String
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(color)
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TagDto> {
        override fun createFromParcel(parcel: Parcel): TagDto {
            return TagDto(parcel)
        }

        override fun newArray(size: Int): Array<TagDto?> {
            return arrayOfNulls(size)
        }
    }
}

fun TagDto.toTag():Tag{
    return Tag(
        name = name,
        color = color
    )
}