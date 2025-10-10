package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.repository.LifeLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLifeLogsByMonthUseCase @Inject constructor(
    private val lifeLogRepository: LifeLogRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<List<LifeLog>> {
        val yearMonth = YearMonth.of(year, month)
        val allDateInMonth = (1..yearMonth.lengthOfMonth()).map {
            "%04d-%02d-%02d".format(year, month, it)
        }

        return combine(
            allDateInMonth.map {
                lifeLogRepository.getLifeLogsByDate(it)
            }
        ) { result ->
            result.toList().flatten()
        }
    }
}