package fr.wesy.sevenminutesworkout.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.wesy.sevenminutesworkout.domain.repository.WorkoutRepository
import fr.wesy.sevenminutesworkout.domain.repository.WorkoutRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideWorkoutRepository(
        @ApplicationContext context: Context
    ) : WorkoutRepository {
        return WorkoutRepositoryImpl(context)
    }
}