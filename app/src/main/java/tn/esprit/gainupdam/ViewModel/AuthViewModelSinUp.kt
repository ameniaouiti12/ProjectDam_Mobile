package tn.esprit.gainupdam.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.gainupdam.dtos.AuthResponse
import tn.esprit.gainupdam.dtos.SignupRequest
import tn.esprit.projectgainup.remote.RetrofitClient

class AuthViewModelSinUp(application: Application) : AndroidViewModel(application) {

    private val _signUpState = mutableStateOf(SignUpState())
    val signUpState: State<SignUpState> = _signUpState

    // Fonction d'inscription
    fun performSignUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            callback(false, "Please fill all fields")
            return
        }

        if (password != confirmPassword) {
            callback(false, "Passwords do not match")
            return
        }

        val signUpRequest = SignupRequest(name, email, password, confirmPassword)

        // Envoi de la requête d'inscription via Retrofit
        RetrofitClient.apiService.signUp(signUpRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    callback(true, "Sign-up successful!")
                } else {
                    val message = response.body()?.message ?: "Sign-up failed!"
                    callback(false, message)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback(false, "Network error: ${t.message}")
            }
        })
    }
}


// Modèle de l'état d'inscription
data class SignUpState(val success: Boolean = false, val message: String = "")