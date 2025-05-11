package com.example.moneymatters.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moneymatters.data.User

@Dao
interface UserDao {
    @Insert
    fun registerUser(user: User)//insert

    @Query("SELECT * FROM users WHERE email= :userName AND password =:password LIMIT 1")
    fun logIn(userName: String, password:String): User?//get
}