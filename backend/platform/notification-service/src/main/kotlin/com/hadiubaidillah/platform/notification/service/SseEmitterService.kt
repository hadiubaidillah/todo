package com.hadiubaidillah.platform.notification.service

import com.hadiubaidillah.platform.notification.entity.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Service
class SseEmitterService {

    private val log = LoggerFactory.getLogger(javaClass)
    private val emitters = ConcurrentHashMap<UUID, CopyOnWriteArrayList<SseEmitter>>()

    fun subscribe(userId: UUID): SseEmitter {
        val emitter = SseEmitter(5 * 60 * 1000L) // 5 minutes timeout
        val userEmitters = emitters.computeIfAbsent(userId) { CopyOnWriteArrayList() }
        userEmitters.add(emitter)
        log.info("SSE subscribe: userId={}, total emitters for user={}", userId, userEmitters.size)

        val cleanup = Runnable {
            userEmitters.remove(emitter)
            if (userEmitters.isEmpty()) {
                emitters.remove(userId, userEmitters)
            }
            log.info("SSE cleanup: userId={}, remaining emitters={}", userId, userEmitters.size)
        }

        emitter.onCompletion(cleanup)
        emitter.onTimeout(cleanup)
        emitter.onError { cleanup.run() }

        return emitter
    }

    fun broadcast(userId: UUID, notification: Notification) {
        val userEmitters = emitters[userId]
        if (userEmitters == null) {
            log.warn("SSE broadcast: no emitters for userId={}, type={}", userId, notification.type)
            return
        }
        log.info("SSE broadcast: userId={}, type={}, emitters={}", userId, notification.type, userEmitters.size)
        userEmitters.forEach { emitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .name("notification")
                        .data(notification)
                )
                log.info("SSE send success: type={}", notification.type)
            } catch (e: Exception) {
                log.error("SSE send failed: type={}, error={}", notification.type, e.message)
                emitter.completeWithError(e)
            }
        }
    }
}
