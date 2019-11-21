package com.example.orangecast.data

sealed class Parameters {

    sealed class Search {

        object Key {
            const val TERM = "term"
            const val COUNTRY = "country"
            const val MEDIA = "media"
            const val ENTITY = "entity"
            const val ATTRIBUTE = "attribute"
            const val LANGUAGE = "lang"
        }

        object Value {
            const val PODCAST = "podcast"
        }
    }
}