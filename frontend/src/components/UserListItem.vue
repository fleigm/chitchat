<template>
  <div class="flex items-center justify-between gap-4 p-1">
    <Avatar label="u" shape="circle" class="bg-blue-600 text-primary-inverse"></Avatar>
    <div class="flex-1">{{ user.username }}</div>
    <Button @click="startChat" icon="pi pi-pencil" class="p-button-rounded p-button-outlined p-button-sm w-6 h-6" />
  </div>
</template>

<script>
import ChatService from "@/api/ChatService";

export default {
  name: "UserListItem",

  props: {
    user: {
      type: Object,
      required: true,
    },
  },

  methods: {
    startChat() {
      ChatService.startChatWith(this.user);
    }
  }
}
</script>

<style scoped>

</style>