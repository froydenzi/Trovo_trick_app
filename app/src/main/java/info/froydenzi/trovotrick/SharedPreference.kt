package info.froydenzi.trovotrick

import android.content.Context
import info.froydenzi.trovotrick.model.Server

class SharedPreference {

    private val context: Context? = null
    private val prefsName = "settings"
    private var settings = context?.getSharedPreferences(prefsName, 0)

    private val serverCountry = "server_country"
    private val serverOVPN = "server_ovpn"
    private val serverUser = "server_ovpn_user"
    private val serverPassword = "server_ovpn_password"
    private val getServer = Server()

    fun saveServer(server: Server) {
        settings?.edit()?.putString(serverCountry, server.getCountry())?.apply()
        settings?.edit()?.putString(serverOVPN, server.getOvpn())?.apply()
        settings?.edit()?.putString(serverUser, server.getOvpnUserName())?.apply()
        settings?.edit()?.putString(serverPassword, server.getOvpnUserPassword())?.apply()
    }

    fun getServer() {
        return getServer.server(
            settings?.getString(serverCountry, "Japan").toString(),
            settings?.getString(serverOVPN, "japan.ovpn").toString(),
            settings?.getString(serverUser, "vpn").toString(),
            settings?.getString(serverPassword, "vpn").toString()
        )
    }
}