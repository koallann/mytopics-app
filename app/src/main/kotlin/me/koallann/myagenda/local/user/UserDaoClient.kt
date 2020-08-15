package me.koallann.myagenda.local.user

import android.content.Context
import io.reactivex.Maybe
import io.reactivex.Single
import me.koallann.myagenda.data.user.UserLocalDataSource
import me.koallann.myagenda.domain.Credentials
import me.koallann.myagenda.domain.User
import me.koallann.myagenda.local.AppDatabase
import me.koallann.support.content.SharedPreferences
import java.util.concurrent.TimeUnit

class UserDaoClient(context: Context) : UserLocalDataSource {

    companion object {
        private const val KEY_SIGNED_EMAIL = "signed_email"
    }

    private val preferences: SharedPreferences = SharedPreferences(context)
    private val userDao: UserDao = AppDatabase.getInstance(context).getUserDao()

    override fun hasUserSigned(): Boolean {
        return preferences.getString(KEY_SIGNED_EMAIL, "").isNotEmpty()
    }

    override fun getSignedUser(): Maybe<User> {
        return Maybe.create { emitter ->
            val signedEmail = preferences.getString(KEY_SIGNED_EMAIL, "")

            if (signedEmail.isEmpty()) {
                emitter.onComplete()
            } else {
                userDao.findByEmail(signedEmail)
                    .map { it.toDomain() }
            }
        }
    }

    override fun signInUser(credentials: Credentials): Single<User> {
        return Single.timer(2, TimeUnit.SECONDS)
            .flatMap { userDao.findByEmail(credentials.email) }
            .flatMap {
                if (it.password == credentials.password) {
                    Single.just(it)
                } else {
                    Single.error(IllegalArgumentException("Wrong password"))
                }
            }
            .map { it.toDomain() }
    }

}