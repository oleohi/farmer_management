package com.oleohialli.farminventory.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FarmerDao {

    @Query("SELECT * FROM farmer_table WHERE farmerName LIKE '%'  || :searchQuery || '%' ORDER BY id DESC")
    fun getFarmers(searchQuery: String): LiveData<List<FarmerInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(farmer: FarmerInfo)

    @Update
    suspend fun update(farmer: FarmerInfo)

    @Delete
    suspend fun delete(farmer: FarmerInfo)

}