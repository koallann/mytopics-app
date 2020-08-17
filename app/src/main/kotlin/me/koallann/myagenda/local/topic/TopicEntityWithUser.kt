package me.koallann.myagenda.local.topic

import androidx.room.Relation
import me.koallann.myagenda.domain.Topic
import me.koallann.myagenda.local.user.UserEntity

class TopicEntityWithUser : TopicEntity() {
    @Relation(parentColumn = "user_id", entityColumn = "id", entity = UserEntity::class)
    override var user: UserEntity = UserEntity()

    fun toDomain(): Topic = Topic(
        id,
        title,
        briefDescription,
        details,
        status,
        user.toDomain()
    )

}
