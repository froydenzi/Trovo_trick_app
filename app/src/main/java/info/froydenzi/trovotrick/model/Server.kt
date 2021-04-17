package info.froydenzi.trovotrick.model

class Server {
    private var country: String = "United States"
    private var ovpn: String = "us.ovpn"
    private var ovpnUserName: String = "freeopenvpn"
    private var ovpnUserPassword: String = "416248023"

    fun server(country: String, ovpn: String) {
        this.country = country
        this.ovpn = ovpn
    }

    fun server(country: String, ovpn: String, ovpnUserName: String, ovpnUserPassword: String) {
        this.country = country
        this.ovpn = ovpn
        this.ovpnUserName = ovpnUserName
        this.ovpnUserPassword = ovpnUserPassword
    }

    fun getCountry(): String {
        return country
    }

    fun setCountry(country: String) {
        this.country = country
    }

    fun getOvpn(): String {
        return ovpn
    }

    fun setOvpn(ovpn: String) {
        this.ovpn = ovpn
    }

    fun getOvpnUserName(): String {
        return ovpnUserName
    }

    fun setOvpnUserName(ovpnUserName: String) {
        this.ovpnUserName = ovpnUserName
    }

    fun getOvpnUserPassword(): String {
        return ovpnUserPassword
    }

    fun setOvpnUserPassword(ovpnUserPassword: String) {
        this.ovpnUserPassword = ovpnUserPassword
    }
}