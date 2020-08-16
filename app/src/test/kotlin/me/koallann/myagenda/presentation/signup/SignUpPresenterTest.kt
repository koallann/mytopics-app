package me.koallann.myagenda.presentation.signup

import android.database.sqlite.SQLiteConstraintException
import io.reactivex.Single
import me.koallann.myagenda.R
import me.koallann.myagenda.data.user.UserRepository
import me.koallann.myagenda.domain.User
import me.koallann.support.handlers.ErrorHandler
import me.koallann.support.rxschedulers.ImmediateSchedulerProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class SignUpPresenterTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var view: SignUpView

    private lateinit var errorHandler: ErrorHandler
    private lateinit var presenter: SignUpPresenter

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        errorHandler = SignUpErrorHandler()
        presenter = SignUpPresenter(userRepository, ImmediateSchedulerProvider(), errorHandler)
        presenter.attachView(view)
    }

    @After
    fun finish() {
        presenter.stop()
        presenter.detachView()
    }

    @Test
    fun `Should not try to sign up if the user fields are invalid`() {
        val user = User("John Doe", "john.doe@acme.com")

        `when`(view.validateUserFields()).thenReturn(false)

        presenter.onClickSignUp(user)

        verify(userRepository, never()).createUser(user)
        verify(view, never()).navigateToHome()
    }

    @Test
    fun `Should try to sign up if the user fields fields are valid`() {
        val user = User("John Doe", "john.doe@acme.com", User.Secret("123456"))

        `when`(view.validateUserFields()).thenReturn(true)
        `when`(userRepository.createUser(user)).thenReturn(Single.just(User()))

        presenter.onClickSignUp(user)

        verify(userRepository, only()).createUser(user)
    }

    @Test
    fun `Should show a specific error message when the given email is already in use`() {
        val user = User("John Doe", "john.doe@acme.com", User.Secret("123456"))
        val error = SQLiteConstraintException("This email is already in use")

        `when`(view.validateUserFields()).thenReturn(true)
        `when`(userRepository.createUser(user)).thenReturn(Single.error(error))

        presenter.onClickSignUp(user)

        verify(view).showMessage(R.string.msg_email_already_used)
        verify(view, never()).navigateToHome()
    }

    @Test
    fun `Should show a specific error message when an unknown errors occurs`() {
        val user = User("John Doe", "john.doe@acme.com", User.Secret("123456"))
        val error = Throwable("Unknown error")

        `when`(view.validateUserFields()).thenReturn(true)
        `when`(userRepository.createUser(user)).thenReturn(Single.error(error))

        presenter.onClickSignUp(user)

        verify(view).showMessage(R.string.msg_cannot_signup)
        verify(view, never()).navigateToHome()
    }

    @Test
    fun `Should sign up if the user fields are okay`() {
        val user = User("John Doe", "john.doe@acme.com", User.Secret("123456"))

        `when`(view.validateUserFields()).thenReturn(true)
        `when`(userRepository.createUser(user)).thenReturn(Single.just(User(user.name, user.email)))

        presenter.onClickSignUp(user)

        verify(view, times(1)).navigateToHome()
    }

}
