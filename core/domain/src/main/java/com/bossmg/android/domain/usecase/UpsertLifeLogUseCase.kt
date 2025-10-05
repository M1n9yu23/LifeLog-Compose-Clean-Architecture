package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.repository.LifeLogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpsertLifeLogUseCase @Inject constructor(
    private val lifeLogRepository: LifeLogRepository
) {
    suspend operator fun invoke(lifeLog: LifeLog) = lifeLogRepository.upsertLifeLog(lifeLog)
}