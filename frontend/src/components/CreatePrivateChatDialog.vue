<template>
  <Button label="start private chat" class="p-button-raised p-button-text" @click="open"/>
  <Dialog header="Start chat" v-model:visible="show" :modal="true" class="w-96">

    <div class="h-96 overflow-y-auto">
      <div v-for="user in users" :key="user.id">
        <div class="flex items-center justify-between gap-4 p-1 cursor-pointer hover:bg-gray-50"
             @click="startChat(user)">
          <Avatar label="u" shape="circle" class="bg-blue-600 text-primary-inverse"></Avatar>
          <div class="flex-1">{{ user.username }}</div>
        </div>
      </div>
    </div>
  </Dialog>
</template>

<script>
import UserService from "@/api/UserService";
import ChatService from "@/api/ChatService";

export default {
  name: "CreatePrivateChatDialog",
  components: {},
  data() {
    return {
      show: false,
    }
  },

  computed: {
    users() {
      const me = UserService.me.value;
      const privateChats = ChatService.privateChats.value;
      const myChatPartners = privateChats.map(chat => chat.participantA.id === me.id ? chat.participantB : chat.participantA);
      return UserService.users.value
          .filter(user => myChatPartners.findIndex(u => user.id === u.id) < 0);
    },
  },

  methods: {
    open() {
      this.show = true;
    },
    close() {
      this.show = false;
    },

    startChat(user) {
      ChatService.startChatWith(user);
      this.close();
    }
  }
}
</script>