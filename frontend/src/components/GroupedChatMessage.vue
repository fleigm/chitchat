<template>
  <div class="col-span-4" :class="fromMe ? 'col-start-2' : ''">
    <Username :user="groupedMessage.sender"></Username>
    <div class="flex flex-col gap-1">
      <div v-for="message in groupedMessage.messages" :key="message.id" class="shadow rounded p-2 bg-blue-100 ">
        {{ message.text }}
      </div>
    </div>
  </div>
</template>

<script>
import UserService from "@/api/UserService";
import Username from "@/components/Username";

export default {
  name: "GroupedChatMessage",
  components: {Username},
  props: {
    groupedMessage: {
      type: Object,
      required: true,
    },
  },

  computed: {
    fromMe() {
      return this.groupedMessage.sender === UserService.me.value.id;
    },
  }
}
</script>
