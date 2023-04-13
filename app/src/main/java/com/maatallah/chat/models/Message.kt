package com.maatallah.chat.models


data class Message(
    val sender: String,
    val receiver: String,
    val text:String,
    val timestamp: Long,
    var isReceived:Boolean = true


) {
    constructor(): this("","","",0)
}
