package com.mytest.composetest.friend

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mytest.composetest.friend.db.FriendsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FriendModule {
    @Provides
    @Singleton
    fun providesContactDatabase(application: Application): FriendsDatabase {
        return Room.databaseBuilder(application.applicationContext, FriendsDatabase::class.java, FriendsDatabase.DB_NAME)
            .addCallback(FriendsDatabase.friendsDbCallback)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE).build()
    }

    @Provides
    @Singleton
    fun providesFriendRepository() = FriendsListRepository()
}

