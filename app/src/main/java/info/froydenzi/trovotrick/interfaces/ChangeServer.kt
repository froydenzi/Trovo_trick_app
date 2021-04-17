package info.froydenzi.trovotrick.interfaces

import info.froydenzi.trovotrick.model.Server

interface ChangeServer {
    fun newServer(server: Server?)
}