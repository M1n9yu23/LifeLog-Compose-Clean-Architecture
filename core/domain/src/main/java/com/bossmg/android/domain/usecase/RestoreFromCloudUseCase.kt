package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.repository.SyncRepository
import com.bossmg.android.domain.usecase.base.SuspendUseCase
import javax.inject.Inject

class RestoreFromCloudUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
) : SuspendUseCase<String, Result<Unit>>() {
    override suspend fun execute(params: String): Result<Unit> = syncRepository.restoreFromCloud(params)
}
