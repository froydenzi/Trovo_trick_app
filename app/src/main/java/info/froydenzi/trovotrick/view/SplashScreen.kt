package info.froydenzi.trovotrick.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.vishnusivadas.advanced_httpurlconnection.PutData
import info.froydenzi.trovotrick.R


@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_fragment)
        val prefsName = "registration"
        val settings = getSharedPreferences(prefsName, 0)

        if ((settings.contains("username")) && (settings.contains("email"))) {

            val handler = Handler(Looper.getMainLooper())
            handler.post {

                val field = arrayOfNulls<String>(2)
                field[0] = "username"
                field[1] = "email"

                val data = arrayOfNulls<String>(2)
                data[0] = settings.getString("username", null)
                data[1] = settings.getString("email", null)

                Log.i("Approve request:", "Username: ${data[0]} Email: ${data[1]}")

                val putData = PutData(
                    "https://froydenzi.000webhostapp.com/login/approved.php", "POST", field, data
                )
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        val result = putData.result
                        Log.i("Approved Response", result)
                        when {
                            result.equals("korisnik_odobren") -> {
                                settings.edit().putBoolean("approved", true).apply()
                            }
                            result.equals("korisnik_neodobren") -> {
                                settings.edit().putBoolean("approved", false).apply()
                            }
                        }
                    }
                }
            }

        } else Log.i("Approved Response", "korisnik nije logovan")

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        }, 1000)
    }
}