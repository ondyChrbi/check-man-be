package cz.fei.upce.checkman.service.authentication

interface AuthenticationService {
    companion object {
        const val MAIL_DELIMITER = "@"

        fun extractStagId(principalName: String) = principalName.substringBefore(MAIL_DELIMITER)
    }
}
