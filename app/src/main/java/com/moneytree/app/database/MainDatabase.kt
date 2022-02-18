package com.moneytree.app.database

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.room.Room
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.repository.network.responses.NSDataUser

object MainDatabase {
    private const val DATABASE_NAME = "money_tree_db"
    private var appDatabase: AppDatabase? = null
    private var nsUserData: NSDataUser? = null

    fun appDatabase(activity: Context?) {
        appDatabase = Room.databaseBuilder(activity!!, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }

    private val isUserDao: Boolean
        get() = appDatabase != null && appDatabase!!.userDao() != null

    //User detail
    fun getUserData(dataCallback: NSUserDataCallback) {
        Thread {
            if (isUserDao) {
                nsUserData = appDatabase!!.userDao()!!.all
            }
            Handler(Looper.getMainLooper()).post {
                if (nsUserData != null) {
                    dataCallback.onResponse(
                        nsUserData!!
                    )
                }
            }
        }.start()
    }

    //Insert User data
    fun insertUserData(userData: NSDataUser, dataCallback: NSUserDataCallback) {
        Thread {
            with(appDatabase!!){
                with(userDao()!!) {
                    deleteAll()
                    insertUsers(userData)
                }
            }
            getUserData(dataCallback)
        }.start()
    }
}