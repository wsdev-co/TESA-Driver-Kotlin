package com.example.tesadriverkotlin


import com.google.gson.Gson
import com.google.gson.JsonParseException
import okhttp3.*
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit
import uk.t3zz.tesadriverkotlin.ThreadEvent


public class TESADriverKotlin(
    private val protocol: String,
    private val host: String,
    private val path: String,
    private val epa: String,
    private val app_type: Int,
    private val account_id: String,
    private val edn: String = ""
) : WebSocketListener() {

    // constants && variables
    private var ws_connection: WebSocket? = null
    private val event_flag: ThreadEvent = ThreadEvent()
    private var driver_buffer: IOFrame? = null
    private val gson: Gson = Gson()
    private val cls_name: String = "TESADriverKotlin"

    public fun open_connection() {
        val client = OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).build()
        val request = Request.Builder().url("$protocol$host/$path")
        request.addHeader("Engine-Permission-Access", epa)
        request.addHeader("App-Type", app_type.toString())
        request.addHeader("Account-ID", account_id)
        request.addHeader("Engine-Dynamic-Hash", edn)
        this.ws_connection = client.newWebSocket(request.build(), this)
    }

    public fun command(cmd: String, payload: MutableMap<String, Any>): Any? {

        if (this.ws_connection != null) {
            // connection established

            // frame preparation
            println("${cls_name}.command: > $cmd >")

            // generating UUID
            val uuid = UUID.randomUUID().toString()

            // calculating hash (SHA256) sum
            val sha256: String = this.sha256(this.serialize(mutableMapOf("cmd" to cmd, "payload" to payload))!!)

            val payload_o: IOFrame = IOFrame(cmd, payload, FrameType.CMD, uuid, null)
            // println("payload_o.sh256: ${payload_o.sha256}")

            // frame encoding
            val payload_s: String? = this.gson.toJson(payload_o, IOFrame::class.java)

            if (payload_s != null) {
                // serialized

                // sending
                val is_sent: Boolean = this.ws_connection!!.send(payload_s)

                if (is_sent) {
                    println("${this.cls_name}.command: sent")

                    event_flag.thread_wait(10)

                    if (this.driver_buffer != null) {
                        // received

                        // frame id validation
                        if (this.driver_buffer!!.cmd_id == uuid) {
                            // expected frame

                            println("${this.cls_name}.command: expected frame")
                            return this.driver_buffer!!.payload
                        } else {
                            // unexpected frame
                            println("${this.cls_name}.command: unexpected frame")
                            return null
                        }
                    } else {
                        // not received
                        println("${this.cls_name}.command: not received")
                        return null
                    }
                } else {
                    // not sent
                    println("${this.cls_name}.command: not sent")
                    return null
                }
            } else {
                // not serialized
                println("${this.cls_name}.command: not serialized")
                return null
            }
        } else {
            // connection not established
            println("${this.cls_name}.command: connection not established")
            return null
        }
    }

    public fun sha256(to_hash: String = "ws.Dev"): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(to_hash.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    public fun serialize(input: Any): String? {
        val output = this.gson.toJson(input)
        println("serialize.output: $output")

        if (output.isEmpty()) {
            return null
        } else {
            return output
        }
    }

    public fun deserialize(input: String): Map<*, *>? {
        val output: Map<*, *> = this.gson.fromJson(input, Map::class.java) as Map<*, *>
        println("deserialize.output: $output")

        if (output.isEmpty()) {
            return null
        } else {
            return output
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("Connected!")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Receiving: $text")

        // frame decoding
        var io_frame: IOFrame?
        try {
            io_frame = this.gson.fromJson(text, IOFrame::class.java)
        } catch (frame_decode_error: JsonParseException) {
            io_frame = null
        }

        if (io_frame != null) {
            // decoded
            if (io_frame.frame_type == FrameType.CMD) {
                // command frame
                println("IOFrame: COMMAND")

                // putting into buffer
                this.driver_buffer = io_frame
                println("${cls_name}.command: < ${io_frame.cmd} <")

                // triggering event flag
                event_flag.thread_notify()
            } else if (io_frame.frame_type == FrameType.SYNC) {
                println("IOFrame: SYNC")
                println("AppSync: $io_frame")
            }
        } else {
            // not decoded
            println("IOFrame: not decoded")
        }

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        println("Closing: $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("Error: $t.message")
    }

}


class IOFrame(
    // data
    val cmd: String,
    val payload: Any,

    // identifiers
    val frame_type: Int,
    val cmd_id: String,
    val sha256: String?
)

public class FrameType {
    companion object {
        const val CMD: Int = +1
        const val SYNC: Int = +3
    }
}


public class AppType {
    public companion object {
        const val T3zz: Int = +1
        const val T3zzMember: Int = +2
        const val T3zzBusiness: Int = +3
    }
}