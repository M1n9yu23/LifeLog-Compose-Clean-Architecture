/*
 * Copyright 2026 Gyugle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private val lifeLogRepository: LifeLogRepository,
) {
    operator fun invoke(year: Int, month: Int): Flow<List<LifeLog>> {
        val yearMonth = YearMonth.of(year, month)
        val allDateInMonth =
            (1..yearMonth.lengthOfMonth()).map {
                "%04d-%02d-%02d".format(year, month, it)
            }

        return combine(
            allDateInMonth.map {
                lifeLogRepository.getLifeLogsByDate(it)
            },
        ) { result ->
            result.toList().flatten()
        }
    }
}
