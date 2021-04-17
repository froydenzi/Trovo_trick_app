package info.froydenzi.trovotrick.view

import android.annotation.SuppressLint
import android.app.Activity
import android.net.VpnService
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.vishnusivadas.advanced_httpurlconnection.PutData
import info.froydenzi.trovotrick.CheckInternetConnection
import info.froydenzi.trovotrick.R
import info.froydenzi.trovotrick.Tools


@Suppress("DEPRECATION")
open class UrlFragment : Fragment() {

    lateinit var vpnStatus: TextView
    lateinit var vpnStatusText: TextView
    var vpnStart = false

    @SuppressLint("InflateParams", "SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val prefsName = "registration"
        val settings = context?.getSharedPreferences(prefsName, 0)

        val viewHome = inflater.inflate(R.layout.fragment_url, container, false)
        val button: Button = viewHome.findViewById(R.id.button_open)
        val buttonStatus: Button = viewHome.findViewById(R.id.button_status)
        val errorText: TextView = viewHome.findViewById(R.id.error_text)
        val text = viewHome.findViewById<TextInputLayout>(R.id.link_input).editText?.text
        val image: ImageView = viewHome.findViewById(R.id.imageView)
        val status = settings!!.getBoolean("approved", false)

        if (status) {
            buttonStatus.background.setTint(requireContext().getColor(R.color.start))
            button.setBackgroundResource(R.drawable.button_style)
            button.isClickable = true
        } else {
            val statusText: TextView = viewHome.findViewById(R.id.status_text)
            statusText.visibility = View.GONE
            buttonStatus.visibility = View.GONE
        }

        image.setOnClickListener {
            val toast = Toast.makeText(
                context,
                R.string.trovo_logo,
                Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.BOTTOM, 0, 60)
            toast.show()
        }

        buttonStatus.setOnClickListener {
            Log.d("VALUE OF STATUS: ", status.toString())

            if (status)
                Toast.makeText(context, "Your account is approved!", Toast.LENGTH_SHORT).show()
            else {
                Toast.makeText(context, "You will not be able to use the application until your account is approved!", Toast.LENGTH_LONG).show()

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
            }
        }

        button.setOnClickListener {
            if (status) {
                errorText.visibility = View.INVISIBLE

                Log.d("SHARING URL", text.toString())
                val validURL: Boolean = URLUtil.isValidUrl(text.toString())

                if (validURL) {
                    settings.edit().putString("trovo_url", text.toString()).apply()

                    val popupView = layoutInflater.inflate(R.layout.fragment_webview, null)
                    val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
                    val vpnButton: Button = popupView.findViewById(R.id.button_vpn)
                    vpnStatus = popupView.findViewById(R.id.vpn_status)
                    vpnStatusText = popupView.findViewById(R.id.vpn_status_text)

                    popupWindow.isTouchable = true
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
                    val webView: WebView = popupView.findViewById(R.id.webView)

                    val urlLink = settings.getString("trovo_url", null)

                    val cookieSync: CookieSyncManager = CookieSyncManager.createInstance(context as Activity)
                    val cookieManager: CookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(webView, true)
                    cookieManager.removeAllCookie()
                    cookieSync.sync()

                    webView.webViewClient = object : WebViewClient() {
                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            handler?.proceed()
                        }

                    }
                    webView.webChromeClient = WebChromeClient()
                    val webSet = webView.settings
                    webSet.javaScriptEnabled = true
                    webSet.domStorageEnabled = true
                    webSet.setAppCacheEnabled(false)
                    webSet.saveFormData = true
                    webSet.savePassword = false
                    webView.clearCache(true)
                    webView.clearHistory()
                    webView.clearFormData()
                    webView.clearMatches()
                    webView.clearSslPreferences()
                    webView.loadUrl(urlLink.toString())
                    Log.w("LINK", urlLink.toString())


                    val buttonStart: Button = popupView.findViewById(R.id.button_start)
                    val progressBar: ProgressBar = popupView.findViewById(R.id.progress_bar)
                    val progressText: TextView = popupView.findViewById(R.id.progress_text)
                    val loadedText: TextView = popupView.findViewById(R.id.loaded_text)
                    var clickable = true
                    var i = 0

                    val handler = Handler()
                    val runnableCode = object : Runnable {
                        override fun run() {
                            handler.postDelayed(this, 1000)
                            if (i < 50) {
                                Log.d("RUN", "Value od i is: $i")
                                webView.reload()
                                cookieManager.removeAllCookie()
                                progressBar.progress = i
                                Log.d("RELOAD", "Reload passed")
                                i++
                            } else {
                                clickable = true
                                buttonStart.background.setTint(requireContext().getColor(R.color.start))
                                buttonStart.text = requireContext().getText(R.string.start)
                                i = 0
                                Log.d("HANDLER", "Should do this once!")
                                handler.removeCallbacks(this)
                            }
                        }
                    }

                    buttonStart.setOnClickListener {
                        if (clickable) {
                            buttonStart.setBackgroundResource(R.drawable.button_style_red)
                            buttonStart.text = requireContext().getText(R.string.stop)
                            progressBar.visibility = View.VISIBLE
                            progressText.visibility = View.VISIBLE
                            loadedText.visibility = View.INVISIBLE
                            clickable = false
                            runnableCode.run()
                        } else {
                            handler.removeCallbacks(runnableCode)
                            popupWindow.dismiss()
                            clickable = true
                        }
                    }

                    vpnButton.setOnClickListener {
                        prepareVpn()
                    }

                } else
                    errorText.visibility = View.VISIBLE
            } else {
                if (status)
                    Toast.makeText(context, "You will not be able to use the application until your account is approved!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context, "You will not be able to use the application if you are not logged in!", Toast.LENGTH_SHORT).show()
            }
        }

        return viewHome
    }
    private fun prepareVpn() {

        if (!vpnStart) {
            if (CheckInternetConnection().netCheck(context)) {

                val intent = VpnService.prepare(context)

                if (intent != null) {
                    startActivityForResult(intent, 1)
                } else Tools(requireContext()).startVpn()

            } else {
                Toast.makeText(context, "You don't have internet connection!", Toast.LENGTH_SHORT).show()
            }
        } else if (Tools(requireContext()).stopVpn()) {
            Toast.makeText(context, "VPN Disconnected successfully!", Toast.LENGTH_SHORT).show()
        }
    }
}
