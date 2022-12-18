package uk.t3zz.tesadriverkotlin// thread_event.kt
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

/**
 * A Kotlin coroutines friendly method of implementing Java wait/notify.
 *
 * Exist safely on mis-matched calls. More than one wait is ignored as
 * is more than one notify if no wait was called. Only works with 2 threads. No notifyall.
 *
 * See Locks
 * https://proandroiddev.com/synchronization-and-thread-safety-techniques-in-java-and-kotlin-f63506370e6d
 */
class ThreadEvent {

    private val channel: Channel<Unit> = Channel<Unit>(0)
    private var is_waiting = false

    /**
     * Wait until Notify is called or the given number of seconds have passed.
     *
     * @param seconds - Max delay before unlock.
     */
    public fun thread_wait(seconds: Long) {
        synchronized(this) {
            if (is_waiting) {
                return
            }
            is_waiting = true
        }

        // block until timeout or notify
        runBlocking {
            // Set up our delay before calling offer.
            val job = launch(Dispatchers.IO) {
                delay((seconds * 1000))
                channel.trySend(Unit).isSuccess
            }
            // This will continue when someone calls offer.
            channel.receive()
            // Somebody called offer. Maybe the timer, maybe saNotify().
            // Channel has released but the timer job may still be running.
            // It's ok to call this even if the job has finished.
            job.cancel()
        }

        synchronized(this) {
            is_waiting = false
        }
    }

    /**
     * Release wait()
     */
    public fun thread_notify() {
        synchronized(this) {
            if (!is_waiting) {
                return
            }
            is_waiting = false 
        }
        channel.trySend(Unit).isSuccess
    }
}



//fun main() {
//
//
//    val thread_flag = ThreadEvent()
//
//    thread {
//        Thread.sleep(2_000)
//        thread_flag.thread_notify()
//    }
//
//    println("before thread lock")
//
//    thread_flag.thread_wait(5)
//
//    println("after thread lock")
//}





