package fr.wesy.sevenminutesworkout.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.wesy.sevenminutesworkout.data.NetworkStatusObserver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideNetworkStatusObserver(@ApplicationContext context: Context): NetworkStatusObserver {
        return NetworkStatusObserver(context)
    }
}