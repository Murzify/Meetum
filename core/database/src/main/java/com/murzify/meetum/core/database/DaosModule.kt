package com.murzify.meetum.core.database

import com.murzify.meetum.core.database.dao.RecordDao
import com.murzify.meetum.core.database.dao.ServiceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
    @Provides
    fun provideServiceDao(db: MeetumDatabase): ServiceDao = db.serviceDao()

    @Provides
    fun provideRecordDao(db: MeetumDatabase): RecordDao = db.recordDao()

}