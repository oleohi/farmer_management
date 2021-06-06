package com.oleohialli.farminventory.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oleohialli.farminventory.Constants
import kotlinx.parcelize.Parcelize

@Entity(tableName = Constants.TABLE_NAME)
@Parcelize
data class FarmerInfo(
    val farmerName: String,
    val farmerPhone: String,
    val farmerState: String,
    val farmerLga: String = "",
    val farmerPhoto: String = "",
    val farmName: String = "",
    val farmLocation: String = "",
    val farmCoordinates: String = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    ) : Parcelable {
}