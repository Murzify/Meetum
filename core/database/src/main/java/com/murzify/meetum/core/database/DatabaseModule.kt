package com.murzify.meetum.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesMeetumDatabase(
        @ApplicationContext context: Context,
    ): MeetumDatabase = Room.databaseBuilder(
        context,
        MeetumDatabase::class.java,
        "meetum-database"
    ).build()

}