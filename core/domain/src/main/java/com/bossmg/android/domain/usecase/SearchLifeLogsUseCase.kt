package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.repository.LifeLogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchLifeLogsUseCase @Inject constructor(
    private val lifeLogRepository: LifeLogRepository,
) {
    suspend operator fun invoke(query: String): List<LifeLog> = lifeLogRepository.searchLifeLogs(query)
}
