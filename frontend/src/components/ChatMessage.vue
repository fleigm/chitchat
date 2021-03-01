<template>
  <div class="col-span-4 shadow rounded p-2 bg-blue-100" :class="fromMe ? 'col-start-2' : ''">
    {{ message.text }}
  </div>
</template>

<script>
import UserService from "@/api/UserService";

export default {
  name: "ChatMessage",

  props: {
    message: {
      type: Object,
      required: true,
    },
  },

  computed: {
    fromMe() {
      return this.message.sender === UserService.me.value.id;
    },


  }
}
</script>

<style scoped>

</style>