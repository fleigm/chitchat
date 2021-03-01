<template>
  <Card class="chat-window">
    <template #title>
      <div class="p-2 border-b">
        <div class="heading">{{ name }}</div>
      </div>
    </template>
    <template #content>
      <div class="">
        <div class="grid grid-cols-5 gap-1">
          <ChatMessage v-for="(msg, i) in messages" :key="i"
                       :message="msg">
          </ChatMessage>
        </div>
      </div>
    </template>
    <template #footer>
        <Textarea class="w-full" rows="1"
                  v-model="message"
                  placeholder="send a message"
                  @keydown.enter.exact.prevent
                  @keydown.enter.shift.exact="newLine"
                  @keyup.enter.exact="sendMessage"></Textarea>
    </template>
  </Card>
</template>

<script>
import ChatSocket from "@/api/ChatSocket";
import ChatService from "@/api/ChatService";
import UserService from "@/api/UserService";
import {Events, EventService} from "@/Events";
import ChatMessage from "@/components/ChatMessage";
import {http} from "@/httpPlugin";

export default {
  name: "ChatWindow",
  components: {ChatMessage},
  data() {
    return {
      chat: {},
      messages: [],
      message: '',
    }
  },

  computed: {
    hasActiveChat() {
      return !!this.chat.id;
    },
    isGroupChat() {
      return ChatService.isGroupChat(this.chat)
    },
    isPrivateChat() {
      return ChatService.isPrivateChat(this.chat)
    },
    name() {
      if (!this.hasActiveChat) {
        return '';
      }
      if (this.isGroupChat.value) {
        return this.chat.name;
      }
      if (this.chat.participantA.id === UserService.me.value.id) {
        return this.chat.participantB.username;
      }
      return this.chat.participantA.username;
    },
  },

  methods: {
    sendMessage(e) {
      if (this.message.trim().length === 0) {
        return;
      }
      const message = {
        chat: this.chat.id,
        text: this.message,
      };
      this.message = '';
      ChatSocket.sendMessage(message);
    },

    newLine() {
      this.message = this.message + '\n';
    },

    registerEvents() {
      EventService.on(Events.OPEN_CHAT, this.openChat);
      EventService.on(Events.NEW_MESSAGE, this.onNewMessage);
    },

    openChat(newChat) {
      this.chat = newChat;
      this.messages = [];
      this.$nextTick(this.fetchMessages);
    },

    onNewMessage(message) {
      if (this.chat.id === message.chat) {
        console.log('push');
        this.messages.push(message)
      }
    },

    fetchMessages() {
      http.get(`chats/${this.chat.id}/messages`)
          .then(({data}) => {
            this.messages.unshift(...data.entries.reverse());
          })
    }
  },

  mounted() {
    this.registerEvents();
  }
}
</script>
