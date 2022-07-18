package com.team.alex.talent.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.team.alex.talent.presentation.utils.RecyclerDiffUtil

data class Question(
    val answer: String,
    val id: String = "",
    val question: String,
    val tags: List<Tag>
):Parcelable, RecyclerDiffUtil.DiffUtilItem{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Tag)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(answer)
        parcel.writeString(id)
        parcel.writeString(question)
        parcel.writeTypedList(tags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Question){
            return when{
                id != other.id -> {false}
                answer != other.answer -> {false}
                question != other.question -> {false}
                tags != other.tags -> {false}
                else -> true
            }
        }
        return super.equals(other)
    }

    override fun areItemsTheSame(other: RecyclerDiffUtil.DiffUtilItem): Boolean {
        return if (other is Question) id == (other).id else false
    }
}
