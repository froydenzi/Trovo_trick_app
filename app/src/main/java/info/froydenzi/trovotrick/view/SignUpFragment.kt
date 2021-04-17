package info.froydenzi.trovotrick.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.vishnusivadas.advanced_httpurlconnection.PutData
import info.froydenzi.trovotrick.R

@Suppress("DEPRECATION")
class SignUpFragment : Fragment() {

    private lateinit var passwordInvalidRepeated: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonSignUp: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewSignUp = inflater.inflate(R.layout.fragment_sign_up, container, false)

        val textFullName = viewSignUp.findViewById<TextInputLayout>(R.id.textInputLayoutFullname)
        val textUsername = viewSignUp.findViewById<TextInputLayout>(R.id.textInputLayoutUsername)
        val textPassword = viewSignUp.findViewById<TextInputLayout>(R.id.textInputLayoutPassword)
        val textPasswordRepeated = viewSignUp.findViewById<TextInputLayout>(R.id.textInputLayoutPasswordRepeat)
        val textEmail = viewSignUp.findViewById<TextInputLayout>(R.id.textInputLayoutEmail)

        val textLogin: TextView = viewSignUp.findViewById(R.id.loginText)
        val takenUsername: TextView = viewSignUp.findViewById(R.id.usedUsername)
        val takenEmail: TextView = viewSignUp.findViewById(R.id.takenEmail)
        val invalidEmail: TextView = viewSignUp.findViewById(R.id.invalidEmail)
        val allFields: TextView = viewSignUp.findViewById(R.id.allFields)
        val nameInvalid: TextView = viewSignUp.findViewById(R.id.invalidName)
        val usernameInvalid: TextView = viewSignUp.findViewById(R.id.invalidUsername)
        val invalidPassword: TextView = viewSignUp.findViewById(R.id.passwordInvalid)

        progressBar = viewSignUp.findViewById(R.id.progress)
        buttonSignUp = viewSignUp.findViewById(R.id.buttonSignUp)
        passwordInvalidRepeated = viewSignUp.findViewById(R.id.passwordRepeated)

        val prefsName = "registration"
        val settings = context?.getSharedPreferences(prefsName, 0)

        buttonSignUp.setOnClickListener {

            val fullName = textFullName.editText?.text
            val username = textUsername.editText?.text
            val password = textPassword.editText?.text
            val passwordRepeated = textPasswordRepeated.editText?.text
            val email = textEmail.editText?.text
            val deviceInfo = settings!!.getString("device", null)

            progressBar.visibility = View.VISIBLE
            buttonSignUp.visibility = View.GONE
            invalidPassword.visibility = View.GONE
            passwordInvalidRepeated.visibility = View.GONE

            takenEmail.visibility = View.GONE
            invalidEmail.visibility = View.GONE
            takenUsername.visibility = View.GONE
            allFields.visibility = View.GONE
            nameInvalid.visibility = View.GONE
            usernameInvalid.visibility = View.GONE

            Log.i("Typed in before checks:", "Name: $fullName Username: $username Password: $password Device: $deviceInfo Email: $email")
            if (isValidName(fullName.toString())) {
                if (isValidUsername(username.toString())) {
                    if (isValidPassword(password.toString(), passwordRepeated.toString())) {
                        if (isValidEmail(email.toString())) {
                            val handler = Handler(Looper.getMainLooper())
                            handler.post {
                                val field = arrayOfNulls<String>(5)
                                field[0] = "fullname"
                                field[1] = "username"
                                field[2] = "password"
                                field[3] = "device"
                                field[4] = "email"

                                val data = arrayOfNulls<String>(5)
                                data[0] = fullName.toString()
                                data[1] = username.toString()
                                data[2] = password.toString()
                                data[3] = deviceInfo
                                data[4] = email.toString()

                                Log.i("After checks:", "Name: ${data[0]} Username: ${data[1]} Password: ${data[2]} Device: ${data[3]} Email: ${data[4]}")

                                val putData = PutData(
                                    "http://froydenzi.000webhostapp.com/login/register.php", "POST", field, data
                                )
                                if (putData.startPut()) {
                                    if (putData.onComplete()) {
                                        val result = putData.result
                                        Log.i("Registration callback", putData.result)
                                        when {
                                            result.equals("registracija_uspjesna") -> {
                                                progressBar.visibility = View.GONE
                                                buttonSignUp.visibility = View.GONE
                                                Toast.makeText(context, "Registration successful, you can proceed to login page!", Toast.LENGTH_LONG).show()
                                            }
                                            result.equals("username_zauzet") -> {
                                                progressBar.visibility = View.GONE
                                                buttonSignUp.visibility = View.VISIBLE
                                                takenUsername.visibility = View.VISIBLE
                                                Toast.makeText(context, takenUsername.text, Toast.LENGTH_SHORT).show()
                                            }
                                            result.equals("email_zauzet") -> {
                                                progressBar.visibility = View.GONE
                                                buttonSignUp.visibility = View.VISIBLE
                                                takenEmail.visibility = View.VISIBLE
                                                Toast.makeText(context, takenEmail.text, Toast.LENGTH_SHORT).show()
                                            }
                                            result.equals("potrebna_sva_polja") -> {
                                                progressBar.visibility = View.GONE
                                                buttonSignUp.visibility = View.VISIBLE
                                                allFields.visibility = View.VISIBLE
                                                Toast.makeText(context, allFields.text, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            invalidEmail.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            buttonSignUp.visibility = View.VISIBLE
                            Toast.makeText(context, invalidEmail.text, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        invalidPassword.visibility = View.VISIBLE
                        Toast.makeText(context, invalidPassword.text, Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                        buttonSignUp.visibility = View.VISIBLE
                    }
                } else {
                    usernameInvalid.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    buttonSignUp.visibility = View.VISIBLE
                    Toast.makeText(context, usernameInvalid.text, Toast.LENGTH_SHORT).show()
                }
            } else {
                nameInvalid.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                buttonSignUp.visibility = View.VISIBLE
                Toast.makeText(context, nameInvalid.text, Toast.LENGTH_SHORT).show()
            }
        }

        textLogin.setOnClickListener {
            textLogin.setTextColor(requireContext().getColor(R.color.teal_700))
            viewSignUp.findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        return viewSignUp
    }

    private fun isValidName(fullname: String?): Boolean {
        fullname?.let {
            val namePattern = "[a-zA-Z']+( [a-zA-Z']+)+"
            val fullNameRegex = Regex(namePattern)
            return fullNameRegex.find(fullname) != null
        } ?: return false
    }

    private fun isValidUsername(username: String?): Boolean {
        username?.let {
            val usernamePattern = "^(?=[a-zA-Z0-9._]{8,20}\$)(?!.*[_.]{2})[^_.].*[^_.]\$"
            val usernameRegex = Regex(usernamePattern)
            return usernameRegex.find(username) != null
        } ?: return false
    }

    private fun isValidPassword(password: String?, passwordRepeated: String?): Boolean {

        password?.let {
            val passwordPattern = "^(?=.*\\d)[0-9a-z]{4,}\$"
            val passwordMatcher = Regex(passwordPattern)

            return if (password == passwordRepeated)
                passwordMatcher.find(password) != null
            else {
                passwordInvalidRepeated.visibility = View.VISIBLE
                Toast.makeText(context, passwordInvalidRepeated.text, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                buttonSignUp.visibility = View.VISIBLE
                false
            }
        } ?: return false
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}