package ru.shvetsov.memehub.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.shvetsov.memehub.domain.repositories.UserRepository
import ru.shvetsov.memehub.domain.usecases.UserUseCase

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideUserUseCase(userRepository: UserRepository): UserUseCase {
        return UserUseCase(userRepository)
    }
}