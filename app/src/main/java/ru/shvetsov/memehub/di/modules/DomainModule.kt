package ru.shvetsov.memehub.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.shvetsov.memehub.domain.repositories.UserRepository
import ru.shvetsov.memehub.domain.repositories.VideoRepository
import ru.shvetsov.memehub.domain.usecases.UserUseCase
import ru.shvetsov.memehub.domain.usecases.VideoUseCase

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideUserUseCase(userRepository: UserRepository): UserUseCase {
        return UserUseCase(userRepository)
    }

    @Provides
    fun provideVideoUseCase(videoRepository: VideoRepository): VideoUseCase {
        return VideoUseCase(videoRepository)
    }
}