<template>
  <div>
    <div class="heading border-b">Users</div>
    <div class="max-h-96 overflow-y-scroll">
      <UserListItem v-for="user in usersWithNoChat" :key="user.id" :user="user"></UserListItem>
    </div>
  </div>
</template>

<script>
import UserService from "@/api/UserService";
import ChatService from "@/api/ChatService";
import UserListItem from "@/components/UserListItem";

export default {
  name: "UserList",
  components: {UserListItem},
  setup() {
    return {
      me: UserService.me,
      users: UserService.users,
      chats: ChatService.chats,
      privateChats: ChatService.privateChats,
    }
  },

  computed: {
    usersWithNoChat() {
      const usersWithChat = this.privateChats
          .map(chat => this.me.id === chat.participantA.id ? chat.participantB.id : chat.participantA.id)

      return this.users.filter(user => !usersWithChat.includes(user.id))
    }
  },


}
</script>
