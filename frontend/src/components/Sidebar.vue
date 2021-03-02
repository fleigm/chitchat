<template>
  <Card>
    <template #title>Chats</template>
    <template #content>
      <div class="max-h-96 overflow-y-auto">
        <div v-for="chat in chats" :key="chat.id"
             class="flex items-center gap-4 p-1">
          <ChatListItem :chat="chat"></ChatListItem>
        </div>
      </div>
    </template>
    <template #footer>
      <div class="flex justify-between">
        <CreatePrivateChatDialog></CreatePrivateChatDialog>
        <CreateGroupChatDialog></CreateGroupChatDialog>
      </div>
    </template>
  </Card>
</template>

<script>
import UserService from "@/api/UserService";
import ChatService from "@/api/ChatService";
import ChatListItem from "@/components/ChatListItem";
import CreatePrivateChatDialog from "@/components/CreatePrivateChatDialog";
import CreateGroupChatDialog from "@/components/CreateGroupChatDialog";

export default {
  name: "Sidebar",
  components: {CreateGroupChatDialog, CreatePrivateChatDialog, ChatListItem},
  setup() {

    return {
      me: UserService.me,
      users: UserService.users,
      chats: ChatService.chats
    }
  }
}
</script>
