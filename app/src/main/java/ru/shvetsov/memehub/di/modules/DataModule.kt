package ru.shvetsov.memehub.di.modules

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.shvetsov.memehub.data.network.token.TokenStorage
import ru.shvetsov.memehub.data.repositories.UserRepositoryImpl
import ru.shvetsov.memehub.domain.repositories.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun providesUserRepository(tokenStorage: TokenStorage): UserRepository {
        return UserRepositoryImpl(tokenStorage)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("token", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTokenStorage(sharedPreferences: SharedPreferences): TokenStorage {
        return TokenStorage(sharedPreferences)
    }
}