package dev.orangecast.shared.data.network.security

expect object CertificatePinner {
    fun pin(hostname: String, certificateHashes: List<String>)
    fun validateCertificate(hostname: String, certificateChain: List<ByteArray>): Boolean
}

object ApiCertificates {
    val ITUNES_API_PINS = listOf(
        "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=", 
        "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys="  
    )
    
    val PODCAST_INDEX_PINS = listOf(
        "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=", 
        "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="  
    )
    
    val LISTEN_NOTES_PINS = listOf(
        "sha256/CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC=", 
        "sha256/DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD="  
    )
}