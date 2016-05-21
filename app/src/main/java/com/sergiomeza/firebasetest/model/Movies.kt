package com.sergiomeza.firebasetest.model

/**
 * Created by Sergio Meza el 5/21/16.
 */
class Movies {
    var name: String = ""
    var genre: String = ""
    var url: String? = null
    var director: String = ""

    constructor() {
    }

    constructor(name: String, genre: String, url: String, director: String) {
        this.name = name
        this.genre = genre
        this.url = url
        this.director = director
    }
}
