package ru.alexeypodusov.sample649.graphfeature.presentation

import android.os.Parcel
import android.os.Parcelable

data class GraphItem(
    val value: Int,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) :
        this(parcel.readInt(), parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(value)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GraphItem> {
        override fun createFromParcel(parcel: Parcel): GraphItem {
            return GraphItem(parcel)
        }

        override fun newArray(size: Int): Array<GraphItem?> {
            return arrayOfNulls(size)
        }
    }
}