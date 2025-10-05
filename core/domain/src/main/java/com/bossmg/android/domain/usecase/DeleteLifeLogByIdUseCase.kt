package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.repository.LifeLogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteLifeLogByIdUseCase @Inject constructor(
    private val lifeLogRepository: LifeLogRepository
){
    suspend operator fun invoke(id: Int) = lifeLogRepository.deleteLifeLogById(id)
}