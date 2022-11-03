package com.mytest.composetest.contact.db

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ContactModule {
    @Provides
    @Singleton
    fun providesContactDatabase(application: Application): FriendsDatabase {
        return Room.databaseBuilder(application.applicationContext, FriendsDatabase::class.java, FriendsDatabase.DB_NAME)
            .addCallback(FriendsDatabase.friendsDbCallback)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE).build()
    }
}

