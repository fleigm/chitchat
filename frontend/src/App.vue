<template>
  <div class="flex gap-4 items-stretch h-full">
    <div class="absolute w-full h-full bg-white z-50 flex flex-col items-center justify-center" v-if="loading">
        <ProgressSpinner></ProgressSpinner>
        <div class="text-xl font-thin text-secondary">Loading...</div>
    </div>
    <Sidebar class="w-96"></Sidebar>
    <div>
      chat
    </div>
  </div>
</template>

<script>
import UserService from '@/api/UserService'
import Sidebar from "@/components/Sidebar";
import ChatService from "@/api/ChatService";

export default {
  name: 'App',
  components: {Sidebar},
  /*setup() {

    onMounted(() => {
      Promise.all([
        UserService.fetchUsers(),
        UserService.fetchMe(),
        ChatService.fetchChats(),
      ]);
    });

    return {
      users: UserService.users,
      me: UserService.me,
    }
  },*/

  data() {
    return {
      users: UserService.users,
      me: UserService.me,
      loading: false,
    }
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
  }
}
</script>
