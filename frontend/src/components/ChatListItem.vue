<template>
  <Avatar :label="icon" shape="circle" class=""></Avatar>
  <div class="">{{ name }}</div>
</template>

<script>
import UserService from "@/api/UserService";

export default {
  name: "ChatListItem",

  props: {
    chat: {
      type: Object,
      required: true,
    },
  },

  computed: {
    name() {
      if (this.isPrivateChat) {
        return this.user.username;
      }
      return this.chat.name;
    },

    icon() {
      return this.isPrivateChat ? 'U' : 'G';
    },

    isPrivateChat() {
      return Object.prototype.hasOwnProperty.call(this.chat,'participantA');
    },

    user() {
      console.log(UserService.me.value.id);
      console.log(this.chat.participantA.id);
      console.log(this.chat.participantB.id);
      console.log('----');

      if (UserService.me.value.id === this.chat.participantA.id) {
        return this.chat.participantB;
      }
      return this.chat.participantA;
    }
  }
}
</script>