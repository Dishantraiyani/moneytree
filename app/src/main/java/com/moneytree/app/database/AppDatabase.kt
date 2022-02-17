package com.moneytree.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moneytree.app.database.dao.NSUserDao
import com.moneytree.app.repository.network.responses.NSDataUser

@Database(entities = [ NSDataUser::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): NSUserDao?
}