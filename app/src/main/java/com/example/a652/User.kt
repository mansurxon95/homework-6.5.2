package com.example.a652

import android.net.Uri
import java.io.Serializable

class User {

    var id:Int? = null
    var lastFirst_name:String? = null
    var tel_number:String? = null
    var davlat:String? = null
    var viloyat:String? = null
    var password:String? = null
    var image:Uri?=null



    constructor(

        id:Int?,
        lastFirst_name: String?,
        tel_number: String?,
        davlat: String?,
        viloyat: String?,
        password: String?,
        image:Uri
    ) {
        this.id = id
        this.lastFirst_name = lastFirst_name
        this.tel_number = tel_number
        this.davlat = davlat
        this.viloyat = viloyat
        this.password = password
        this.image = image
    }

    constructor()
}