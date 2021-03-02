<template>
  <span>{{ name }}</span>
</template>

<script>
import UserService from "@/api/UserService";

export default {
  name: "Username",

  props: {
    user: {
      required: true,
    }
  },

  computed: {
    name() {
      const id = this.user.id ? this.user.id : this.user;
      return UserService.users.value.find(user => user.id === id).username;
    }
  }
}
</script>