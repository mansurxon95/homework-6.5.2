package com.example.a652

interface MyDBservis {

    fun addUser(user:User)
    fun editUser(user:User):Int
    fun deletUser(user:User)
    fun getallUser(): ArrayList<User>

}
