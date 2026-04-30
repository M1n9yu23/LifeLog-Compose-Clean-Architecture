package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.repository.SyncRepository
import javax.inject.Inject

class CancelSyncUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
) {
    operator fun invoke() = syncRepository.cancelAllSync()
}
