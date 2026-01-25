package com.example.myfinalproject.data

import android.os.Parcelable
import com.example.myfinalproject.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceData(
    val name: String,
    val position: LatLng,
    val description: String,
    val imageRes: Int = R.drawable.default_building, // default
    val alamat: String = "",
    val coordinateStr: String = "",
    val nomor: String = "",
    val periode: String = "",
    val jenis: String = ""
) : Parcelable

