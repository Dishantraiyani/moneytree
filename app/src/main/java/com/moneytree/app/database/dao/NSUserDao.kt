package com.moneytree.app.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.moneytree.app.repository.network.responses.NSDataUser

@Dao
interface NSUserDao {
    @get:Query("SELECT * FROM nsdatauser")
    val all: NSDataUser?

    @Insert
    fun insertUsers(vararg userDetail: NSDataUser?)

    @Query("DELETE FROM nsdatauser")
    fun deleteAll()
}