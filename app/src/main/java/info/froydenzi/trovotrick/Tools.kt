package info.froydenzi.trovotrick

import android.content.Context
import android.os.RemoteException
import android.view.View
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import info.froydenzi.trovotrick.model.Server
import info.froydenzi.trovotrick.view.UrlFragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class Tools(private val context_tools: Context) :UrlFragment() {

    val server: Server = Server()

    fun startVpn() {
        try {

            val conf = context_tools.assets.open(server.getOvpn())
            val isr = InputStreamReader(conf)
            val br = BufferedReader(isr)
            var config = ""
            var line: String?
            while (true) {

                line = br.readLine()
                if (line == null) break
                config += """
                $line
                
                """.trimIndent()
            }
            br.readLine()
            OpenVpnApi.startVpn(context_tools, config, server.getCountry(), server.getOvpnUserName(), server.getOvpnUserPassword())

            vpnStart = true
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun stopVpn(): Boolean {
        try {
            OpenVPNThread.stop()
            vpnStart = false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun setStatus(connectionState: String?) {
        if (connectionState != null) {

            vpnStatusText.visibility = View.VISIBLE
            vpnStatus.visibility = View.VISIBLE

            when (connectionState) {

                "DISCONNECTED" -> {
                    vpnStart = false
                    OpenVPNService.setDefaultStatus()
                    vpnStatus.text = ""
                }
                "CONNECTED" -> {
                    vpnStart = true
                    vpnStatus.text = context?.getString(R.string.connected)
                }
                "WAIT" -> vpnStatus.text = context?.getString(R.string.waiting)
                "AUTH" -> vpnStatus.text = context?.getString(R.string.server_auth)
                "RECONNECTING" -> vpnStatus.text = context?.getString(R.string.reconnecting)
                "NONETWORK" -> vpnStatus.text = context?.getString(R.string.no_internet)
            }
        }
    }

    /*fun getServerList(): ArrayList<*> {
        val servers: ArrayList<Unit> = ArrayList()
        val server = Server()
        servers.add(
            server.server(
                "United States",
                "us.ovpn",
                "freeopenvpn",
                "416248023"
            )
        )
        servers.add(
            Server().server(
                "Japan",
                "japan.ovpn",
                "vpn",
                "vpn"
            )
        )
        servers.add(
            Server().server(
                "Sweden",
                "sweden.ovpn",
                "vpn",
                "vpn"
            )
        )
        servers.add(
            Server().server(
                "Korea",
                "korea.ovpn",
                "vpn",
                "vpn"
            )
        )
        return servers
    }*/

    override fun onStop() {
        SharedPreference().saveServer(server)
        stopVpn()
        super.onStop()
    }
}
