package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.repository.LifeLogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetImagesUseCase @Inject constructor(
    private val lifeLogRepository: LifeLogRepository
) {
    operator fun invoke() = lifeLogRepository.getImages()
}