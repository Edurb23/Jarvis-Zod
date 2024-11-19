package br.com.zod.service

import br.com.zod.model.ConsumoDispositivo
import br.com.zod.model.ConsumoTotal
import br.com.zod.model.Device
import br.com.zod.model.ListDevices
import br.com.zod.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // Usuario

    @POST("/register/email")
    fun registerEmail(@Body user: Map<String, String>): Call<Map<String, Any>>

    @POST("/register/userinfo")
    fun registerUserInfo(@Body user: Map<String, String>): Call<Map<String, Any>>

    @POST("/register/phone")
    fun registerPhone(@Body phoneData: Map<String, String>): Call<Map<String, Any>>

    @POST("/login")
    fun login(@Body credentials: Map<String, String>): Call<Map<String, String>>


    @GET("/usuario")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<User>

    @PUT("/email")
    fun updateEmail(
        @Header("Authorization") token: String,
        @Body userData: Map<String, String>
    ): Call<Void>

    @PUT("/usuario")
    fun updateUser(
        @Header("Authorization") token: String,
        @Body userData: Map<String, String>
    ): Call<Void>


    ///// DEVICES

    @GET("/dispositivos")
    fun getDevices(
        @Header("Authorization") token: String
    ): Call<ListDevices>


    @POST("/dispositivo")
    fun registerDevice(
        @Header("Authorization") token: String,
        @Body device: Device
    ): Call<Map<String, Any>>


    @DELETE("/dispositivo/{name}")
    fun deleteDevice(
        @Header("Authorization") token: String,
        @Path("name") deviceName: String
    ): Call<Void>


    ///// RELATORIOS

    @GET("/consumo")
    fun getConsumoTotal(
        @Header("Authorization") token: String
    ): Call<ConsumoTotal>


    @GET("/consumo/detalhado")
    fun getConsumoDetalhado(
        @Header("Authorization") token: String
    ): Call<List<ConsumoDispositivo>>



}