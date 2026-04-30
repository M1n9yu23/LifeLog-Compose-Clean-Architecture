package com.bossmg.android.domain.usecase

import com.bossmg.android.domain.repository.SyncRepository
import com.bossmg.android.domain.usecase.base.FlowUseCase
import com.bossmg.android.domain.usecase.base.NoParams
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSyncingStateUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
) : FlowUseCase<NoParams, Boolean>() {
    override fun execute(params: NoParams): Flow<Boolean> = syncRepository.isSyncing
}
