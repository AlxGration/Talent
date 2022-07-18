package com.team.alex.talent.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.team.alex.talent.presentation.utils.RecyclerDiffUtil

data class Vacancy(
    val id: String,
    val name: String,
) : Parcelable, RecyclerDiffUtil.DiffUtilItem {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Vacancy> {
        override fun createFromParcel(parcel: Parcel): Vacancy {
            return Vacancy(parcel)
        }

        override fun newArray(size: Int): Array<Vacancy?> {
            return arrayOfNulls(size)
        }
    }

    override fun areItemsTheSame(other: RecyclerDiffUtil.DiffUtilItem): Boolean {
        return if (other is Vacancy) id == (other).id else false
    }
}

