package com.oleohialli.farminventory.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oleohialli.farminventory.Constants
import com.oleohialli.farminventory.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [FarmerInfo::class], version = Constants.DATABASE_VERSION_NUMBER)
abstract class FarmerDatabase : RoomDatabase() {

    abstract fun farmerDao(): FarmerDao

    class Callback @Inject constructor(
        private val database: Provider<FarmerDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao = database.get().farmerDao()
            applicationScope.launch {
                dao.insert(FarmerInfo("Edouard Mendy", "080938948739", "Kano"))
                dao.insert(FarmerInfo("Paulo Ferreira", "080938948739", "Kano"))
                dao.insert(FarmerInfo("Thelma Obosi", "080938948739", "Kano"))
                dao.insert(FarmerInfo("Sue Ikpuri", "080938948739", "Kano"))
                dao.insert(FarmerInfo("Karo Smith", "080938948739", "Kano"))
            }
        }
    }
}