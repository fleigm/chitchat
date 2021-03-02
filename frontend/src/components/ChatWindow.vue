<template>
  <Card class="chat-window" v-if="hasActiveChat">
    <template #title>
      <div class="p-2 border-b">
        <div class="heading">{{ name }}</div>
      </div>
    </template>
    <template #content>
      <div class="flex flex-col h-full">
        <div class="h-0 flex-grow overflow-y-scroll" ref="scrollContainer" @scroll="scroll">
          <div class="grid grid-cols-5 gap-1" ref="scrollContent">
            <template v-if="isPrivateChat">
              <ChatMessage v-for="(msg, i) in messages" :key="i"
                           :message="msg">
              </ChatMessage>
            </template>
            <template v-else>
              <GroupedChatMessage v-for="(groupedMessage, i) in groupedMessages" :key="i"
                                  :groupedMessage="groupedMessage">
              </GroupedChatMessage>
            </template>
          </div>
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
import GroupedChatMessage from "@/components/GroupedChatMessage";

export default {
  name: "ChatWindow",
  components: {GroupedChatMessage, ChatMessage},
  data() {
    return {
      chat: {},
      messages: [],
      latestPage: {},
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
      if (this.isGroupChat) {
        return this.chat.name;
      }
      if (this.chat.participantA.id === UserService.me.value.id) {
        return this.chat.participantB.username;
      }
      return this.chat.participantA.username;
    },

    groupedMessages() {
      if (this.messages.length === 0) {
        return
      }

      let currentGroup = this.createMewGroup(this.messages[0]);
      const grouped = [currentGroup];


      for (let i = 1; i < this.messages.length; i++) {
        const message = this.messages[i];
        if (currentGroup.sender === message.sender) {
          currentGroup.messages.push(message);
        } else {
          currentGroup = this.createMewGroup(message);
          grouped.push(currentGroup);
        }
      }

      return grouped;
    }
  },

  methods: {
    createMewGroup(message) {
      return {
        sender: message.sender,
        messages: [message],
      }
    },
    scroll(e) {
      const container = this.$refs.scrollContainer;
      const content = this.$refs.scrollContent;
      const position = container.scrollTop * 100 / (content.offsetHeight - container.offsetHeight);

      if (position === 0) {
        this.fetchOlderMessages();
      }
    },

    scrollToLastMessage() {
      const container = this.$refs.scrollContainer;
      const content = this.$refs.scrollContent;

      container.scrollTop = content.offsetHeight - container.offsetHeight;
    },

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
      this.fetchMessages().then(this.scrollToLastMessage);
    },

    onNewMessage(message) {
      if (this.chat.id === message.chat) {
        console.log('push');
        this.messages.push(message)

        if (UserService.me.value.id === message.sender) {
          this.$nextTick(this.scrollToLastMessage);
        }
      }
    },

    fetchMessages(page = 1) {
      return http.get(`chats/${this.chat.id}/messages?page=${page}`)
          .then(({data}) => {
            this.latestPage = data;
            this.messages.unshift(...data.entries.reverse());
          })
    },

    fetchOlderMessages() {
      if (this.latestPage.currentPage === this.latestPage.lastPage) {
        return;
      }

      const container = this.$refs.scrollContainer;
      const content = this.$refs.scrollContent;
      const oldHeight = content.offsetHeight;

      this.fetchMessages(this.latestPage.currentPage + 1)
          .then(() => {
            this.$nextTick(() => {
              container.scrollTop = content.offsetHeight - oldHeight
            })
          })
    },
  },

  mounted() {
    this.registerEvents();
  }
}
</script>
