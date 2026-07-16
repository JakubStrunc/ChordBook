package cz.jstrunc.chordbook.android.data.api

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

fun getApiErrorMessage(
    exception: Exception
): String {
    return when (exception) {
        is UnknownHostException ->
            "Není dostupné připojení k internetu."

        is ConnectException ->
            "Server není dostupný."

        is SocketTimeoutException ->
            "Server neodpověděl včas."

        is SSLException ->
            "Nepodařilo se vytvořit zabezpečené spojení."

        is HttpException -> when (exception.code()) {
            401 -> "Přihlášení vypršelo."
            403 -> "K této akci nemáte oprávnění."
            404 -> "Požadovaná data nebyla nalezena."
            in 500..599 -> "Na serveru nastala chyba."
            else -> "Server vrátil chybu ${exception.code()}."
        }

        else ->
            "Nepodařilo se připojit k serveru."
    }
}