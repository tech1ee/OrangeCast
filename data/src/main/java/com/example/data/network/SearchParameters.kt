package com.example.data.network

sealed class Parameters {

    sealed class Search {

        object Key {
            const val TERM = "term"
            const val COUNTRY = "country"
            const val MEDIA = "media"
            const val ENTITY = "entity"
            const val ATTRIBUTE = "attribute"
            const val LANGUAGE = "lang"
            const val LIMIT = "limit"
        }

        object Value {
            const val PODCAST = "podcast"
            const val MAX_LIMIT = "200"
        }
    }
}