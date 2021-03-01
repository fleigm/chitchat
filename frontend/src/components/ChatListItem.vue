<template>
  <div class="cursor-pointer flex gap-4 items-center" @click="openChat">
    <Avatar :label="icon" shape="circle" class="text-primary-inverse"></Avatar>
    <div class="">{{ name }}</div>
  </div>
</template>

<script>
import UserService from "@/api/UserService";
import {Events} from "@/Events";

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
      if (UserService.me.value.id === this.chat.participantA.id) {
        return this.chat.participantB;
      }
      return this.chat.participantA;
    }
  },

  methods: {
    openChat() {
      this.$eventService.emit(Events.OPEN_CHAT, this.chat);
    }
  }
}
</script>