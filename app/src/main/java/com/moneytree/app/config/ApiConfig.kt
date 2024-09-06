package com.moneytree.app.config

object ApiConfig {

    val key = "poOtBNUmOwydFoDBVnHWlknxz8DjxCrr2CZzeXgF04E="
    val baseUrl : String
        get() = Security.decrypt("bRgWk0yhw59EGSDZ7zqyn8lCMmsFqK9DVgAZEVQDtfim74CKh2EMwl+/Pgk7IPiX", Security.stringToSecretKey(key))

    val baseUrlImage : String
        get() = baseUrl + Security.decrypt("LQsyERBezv7WOAmdvOOr0I6RMFx2Hp+Qt8j2QQE6f+M=", Security.stringToSecretKey(key))

    val baseUrlImagePopUp : String
        get() = baseUrl + Security.decrypt("N/8pZqsNqskj+fMquNXvS5XZ2j1yDARuJigKKkzV1ag=", Security.stringToSecretKey(key))

    val baseUrlImageCategory : String
        get() = baseUrl + Security.decrypt("d/oqsBfQwrcYs/uiLm+VLWvgWNVsXLXy9kwADXMYy4LCu0Zxqqiz/lQ5BIWq6vMs", Security.stringToSecretKey(key))

    val terms : String
        get() = baseUrl + Security.decrypt("qyKM+BrdgBEwPiPhZO6eZDUbiwYlRetrJC+3QozHC0xiQ58p/5iUD9KbLaa20YOK", Security.stringToSecretKey(key))

    val refund : String
        get() = baseUrl + Security.decrypt("LIHpANa3Typy/0OLQZT2j+5XkyU+tsjFuo/op3jA1wobnWPTII2jbANbIJsx17JH", Security.stringToSecretKey(key))

    val policy : String
        get() = baseUrl + Security.decrypt("xL3BslVedjkUKCs9BJdBqthzT9vbEbOSFyjX6+xr0js=", Security.stringToSecretKey(key))

    val meetingImages : String
        get() = baseUrl + Security.decrypt("0IsCQqvzAaXIlkUMaN6qM22aZAiOjwIXcSrW6gciD1Bu2CwcNyh5qjF6AQcfeEhC", Security.stringToSecretKey(key))

    //Security.encrypt("privacy-policy", Security.stringToSecretKey("poOtBNUmOwydFoDBVnHWlknxz8DjxCrr2CZzeXgF04E="))
}