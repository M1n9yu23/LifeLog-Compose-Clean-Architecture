package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.repository.LifeLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLifeLogsByDateUseCase @Inject constructor(
    private val lifeLogRepository: LifeLogRepository
) {
    operator fun invoke(date: String): Flow<List<LifeLog>> = lifeLogRepository.getLifeLogsByDate(date)
}