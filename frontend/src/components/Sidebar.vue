<template>
  <div class="bg-gray-100 border">
    <div class="heading">
      ChitChat
    </div>
    <div class="border-t border-b">
      <div class="heading border-b">Chats</div>
      <div class="max-h-96 overflow-y-scroll">
        <div v-for="chat in chats" :key="chat.id"
             class="flex items-center gap-4 p-1">
          <ChatListItem :chat="chat"></ChatListItem>
        </div>
      </div>
    </div>
    <UserList></UserList>
  </div>
</template>

<script>
import UserService from "@/api/UserService";
import ChatService from "@/api/ChatService";
import ChatListItem from "@/components/ChatListItem";
import UserList from "@/components/UserList";

export default {
  name: "Sidebar",
  components: {UserList, ChatListItem},
  setup() {

    return {
      me: UserService.me,
      users: UserService.users,
      chats: ChatService.chats
    }
  }
}
</script>
