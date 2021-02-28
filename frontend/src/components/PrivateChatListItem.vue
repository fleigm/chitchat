<template>

</template>

<script>
import UserService from "@/api/UserService";

export default {
  name: "PrivateChatListItem",

  props: {
    chat: {
      type: Object,
      required: true,
    },
  },

  computed: {
    user() {
      if (UserService.me.value.id === this.chat.participantA.id) {
        return this.chat.participantB;
      }
      return this.chat.participantA;
    }
  }
}
</script>