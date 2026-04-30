package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.repository.SyncRepository
import com.bossmg.android.domain.usecase.base.NoParams
import com.bossmg.android.domain.usecase.base.SuspendUseCase
import javax.inject.Inject

class SyncNowUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
) : SuspendUseCase<NoParams, Result<Unit>>() {
    override suspend fun execute(params: NoParams): Result<Unit> = syncRepository.syncNow()
}
