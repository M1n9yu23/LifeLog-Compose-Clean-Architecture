package com.bossmg.android.data

import com.bossmg.android.data.mapper.LifeLogMapper
import com.bossmg.android.data.model.LifeLogEntity
import com.bossmg.android.domain.model.LifeLog
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LifeLogMapperTest {
    private lateinit var mapper: LifeLogMapper

    @Before
    fun setUp() {
        mapper = LifeLogMapper()
    }

    @Test
    fun mapEntityToDomain() {
        val entity = LifeLogEntity(
            id = 1,
            date = "2025-10-05",
            title = "Test Title",
            description = "Test Description",
            mood = "\uD83D\uDE0A 기쁨",
            img = "image.png"
        )


        val domainModel = mapper.map(entity)

        assertEquals(entity.id, domainModel.id)
        assertEquals(entity.title, domainModel.title)
        assertEquals(entity.date, domainModel.date)
        assertEquals(entity.mood, domainModel.mood)
        assertEquals(entity.description, domainModel.description)
        assertEquals(entity.img, domainModel.img)
    }

    @Test
    fun mapBackDomainToEntity() {
        val domainModel = LifeLog(
            id = 2,
            date = "2025-10-06",
            title = "Domain Title",
            description = "Domain Description",
            mood = "\uD83D\uDE22 슬픔",
            img = "test.png"
        )

        val entity = mapper.mapBack(domainModel)

        assertEquals(domainModel.id, entity.id)
        assertEquals(domainModel.title, entity.title)
        assertEquals(domainModel.date, entity.date)
        assertEquals(domainModel.mood, entity.mood)
        assertEquals(domainModel.description, entity.description)
        assertEquals(domainModel.img, entity.img)
    }
}