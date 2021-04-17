package info.froydenzi.trovotrick.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.vishnusivadas.advanced_httpurlconnection.PutData
import info.froydenzi.trovotrick.R


class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewLogin = inflater.inflate(R.layout.fragment_login, container, false)

        val textUsername = viewLogin.findViewById<TextInputLayout>(R.id.textInputLayoutUsername)
        val textPassword = viewLogin.findViewById<TextInputLayout>(R.id.textInputLayoutPasswordRepeat)

        val signUpText: TextView = viewLogin.findViewById(R.id.signUpText)
        val loginButton: Button = viewLogin.findViewById(R.id.buttonLogin)
        val progressBar: ProgressBar = viewLogin.findViewById(R.id.progress)
        val loginFailed: TextView = viewLogin.findViewById(R.id.loginFailed)
        val passwordFailed: TextView = viewLogin.findViewById(R.id.loginPassword)

        val prefsName = "registration"
        val settings = context?.getSharedPreferences(prefsName, 0)

        loginButton.setOnClickListener {

            val username = textUsername.editText?.text
            val password = textPassword.editText?.text

            progressBar.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
            loginFailed.visibility = View.GONE
            passwordFailed.visibility = View.GONE

            Log.i("Typed in before checks:", "Username: $username Password: $password")

            if ((username!!.isNotEmpty()) && (password!!.isNotEmpty())) {
                val handler = Handler(Looper.getMainLooper())
                handler.post {

                    val field = arrayOfNulls<String>(2)
                    field[0] = "username"
                    field[1] = "password"

                    val data = arrayOfNulls<String>(2)
                    data[0] = username.toString()
                    data[1] = password.toString()

                    Log.i("After checks:", "Username: ${data[0]} Password: ${data[1]}")

                    val putData = PutData(
                        "https://froydenzi.000webhostapp.com/login/login.php", "POST", field, data
                    )
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            val result = putData.result
                            Log.i("Login Response", result)

                            if (!result.equals("login_neuspjesan")) {
                                if (!result.equals("password_netocan")) {
                                    val loginArray: List<String> = result.split("-") //Should have 3 items,backend stuff
                                    val approvedArray: List<String> = loginArray[2].split("_")

                                    if (approvedArray[0] == "uspjesan") {

                                        progressBar.visibility = View.GONE
                                        loginButton.visibility = View.GONE

                                        settings?.edit()?.putString("fullname", loginArray[0])?.apply()
                                        settings?.edit()?.putString("username", username.toString())?.apply()
                                        settings?.edit()?.putString("email", loginArray[1])?.apply()
                                        settings?.edit()?.putBoolean("status", true)?.apply()

                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()

                                        when {
                                            approvedArray[1] == "odobren" -> {
                                                settings?.edit()?.putBoolean("approved", true)?.apply()
                                            }
                                            approvedArray[1] == "neodobren" -> {
                                                settings?.edit()?.putBoolean("approved", false)?.apply()
                                            }
                                        }
                                        startActivity(Intent(context, MainActivity::class.java))


                                    } else {
                                        progressBar.visibility = View.GONE
                                        loginButton.visibility = View.VISIBLE
                                        loginFailed.visibility = View.VISIBLE
                                        Toast.makeText(context, "Login failed,server error!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    progressBar.visibility = View.GONE
                                    loginButton.visibility = View.VISIBLE
                                    passwordFailed.visibility = View.VISIBLE
                                    Toast.makeText(context, passwordFailed.text, Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                progressBar.visibility = View.GONE
                                loginButton.visibility = View.VISIBLE
                                loginFailed.visibility = View.VISIBLE
                                Toast.makeText(context, loginFailed.text, Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
            } else {
                progressBar.visibility = View.GONE
                loginButton.visibility = View.VISIBLE
                Toast.makeText(context, "All fields are required for login process!", Toast.LENGTH_LONG).show()
            }
        }
        signUpText.setOnClickListener {
            signUpText.setTextColor(requireContext().getColor(R.color.teal_700))
            viewLogin.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        return viewLogin
    }
}