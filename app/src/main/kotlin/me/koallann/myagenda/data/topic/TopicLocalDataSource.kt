package me.koallann.myagenda.data.topic

import io.reactivex.Completable
import io.reactivex.Single
import me.koallann.myagenda.domain.Topic
import me.koallann.myagenda.domain.User

interface TopicLocalDataSource {

    fun getTopicsByUser(user: User): Single<List<Topic>>

    fun createTopic(topic: Topic): Completable

    fun updateTopicStatus(topic: Topic): Completable

}
