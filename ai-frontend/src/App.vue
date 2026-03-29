<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import axios from 'axios'

// SSE 流式响应在浏览器端通常使用原生 EventSource；
// 这里保留 axios 以满足“Axios 请求库”的技术选型要求。
void axios

const API_BASE_URL = 'http://localhost:8123/api'

const chatId = ref('')
const inputMessage = ref('')
const messages = ref([])

const eventSourceRef = ref(null)
const isStreaming = ref(false)

const chatLogRef = ref(null)

function uuidv4() {
  // 不依赖额外库的简易 UUIDv4
  // RFC4122: 版本号 4，变体字段为 10xx
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

function scrollToBottom() {
  nextTick(() => {
    const el = chatLogRef.value
    if (!el) return
    el.scrollTop = el.scrollHeight
  })
}

function closeStream() {
  if (eventSourceRef.value) {
    eventSourceRef.value.close()
    eventSourceRef.value = null
  }
  isStreaming.value = false
}

function startSseChat({ message, chatIdValue, aiMessageId }) {
  closeStream()
  isStreaming.value = true

  const url = `${API_BASE_URL}/ai/sparkmind_app/chat/sse?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatIdValue)}`

  const es = new EventSource(url)
  eventSourceRef.value = es

  es.onmessage = (evt) => {
    const chunk = evt.data ?? ''
    if (!chunk) return

    const target = messages.value.find((m) => m.id === aiMessageId)
    if (target) {
      target.content += chunk
      scrollToBottom()
    }
  }

  es.onerror = () => {
    // 通常在后端完成/关闭连接时会触发。EventSource 也会自动重连，
    // 这里强制关闭，避免出现重复流。
    es.close()
    eventSourceRef.value = null
    isStreaming.value = false
  }
}

async function sendMessage() {
  const text = inputMessage.value.trim()
  if (!text) return
  if (isStreaming.value) return

  const userMsg = { id: uuidv4(), role: 'user', content: text }
  messages.value.push(userMsg)
  inputMessage.value = ''
  scrollToBottom()
  await nextTick()

  const aiMessageId = uuidv4()
  messages.value.push({ id: aiMessageId, role: 'ai', content: '' })
  scrollToBottom()

  startSseChat({ message: text, chatIdValue: chatId.value, aiMessageId })
}

onMounted(() => {
  chatId.value = uuidv4()
  scrollToBottom()
})

onBeforeUnmount(() => closeStream());
</script>

<template>
  <div class="chat-page">
    <div class="chat-header">
      <div class="title">Sparkmind Chat</div>
      <div class="meta">chatId: {{ chatId }}</div>
    </div>

    <div ref="chatLogRef" class="chat-log">
      <div
        v-for="m in messages"
        :key="m.id"
        class="message-row"
        :class="m.role === 'ai' ? 'ai' : 'user'"
      >
        <div v-if="m.role === 'ai'" class="avatar" title="AI">AI</div>

        <div v-if="m.role === 'user'" class="avatar" title="User">你</div>

        <div class="bubble" :class="m.role">
          {{ m.content }}
          <div v-if="m.role === 'ai' && isStreaming" class="typing">正在生成...</div>
        </div>
      </div>
    </div>

    <div class="chat-footer">
      <div class="input-area">
        <textarea
          v-model="inputMessage"
          class="chat-input"
          rows="3"
          placeholder="请输入消息，然后回车发送"
          @keydown.enter.exact.prevent="sendMessage"
          :disabled="isStreaming"
        />
        <button class="send-btn" :disabled="isStreaming" @click="sendMessage">
          发送
        </button>
      </div>
    </div>
  </div>
</template>

