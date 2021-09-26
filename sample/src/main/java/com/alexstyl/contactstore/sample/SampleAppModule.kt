package com.alexstyl.contactstore.sample

import android.app.Application
import com.alexstyl.contactstore.ContactStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SampleAppModule {

    @Provides
    fun permissions(application: Application): ContactPermission {
        return AndroidContactPermission(application)
    }

    @Provides
    fun contactStore(application: Application): ContactStore {
        return ContactStore.newInstance(application)
    }
}
