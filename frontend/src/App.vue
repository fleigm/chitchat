<template>
  <div class="flex gap-4 items-stretch h-full bg-gray-100 p-4">
    <div class="absolute w-full h-full bg-white z-50 flex flex-col items-center justify-center" v-if="loading">
        <ProgressSpinner></ProgressSpinner>
        <div class="text-xl font-thin text-secondary">Loading...</div>
    </div>
    <div class="w-96 h-full">
      <Sidebar class="h-full"></Sidebar>
    </div>
    <div class="flex-1">
      <ChatWindow class=""></ChatWindow>
    </div>
  </div>
</template>

<script>
import UserService from '@/api/UserService'
import Sidebar from "@/components/Sidebar";
import ChatService from "@/api/ChatService";
import ChatWindow from "@/components/ChatWindow";
import ChatSocket from "@/api/ChatSocket";

export default {
  name: 'App',
  components: {ChatWindow, Sidebar},

  data() {
    return {
      users: UserService.users,
      me: UserService.me,
      loading: false,
    }
  },

  computed: {

  },

  mounted() {
    this.loading = true;
    Promise.all([
      UserService.fetchUsers(),
      UserService.fetchMe(),
      ChatService.fetchChats(),
    ]).finally(() => {
      this.loading = false;
    })
    ChatSocket.connect();
  }
}
</script>
