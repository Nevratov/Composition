package com.example.composition.domain.useCases

import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.repository.GameRepository
import com.example.composition.domain.entity.Level

class GetGameSettingsUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(level: Level): GameSettings {
        return repository.getGameSettings(level)
    }
}