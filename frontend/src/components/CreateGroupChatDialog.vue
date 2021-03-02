<template>
  <Button label="start group chat" class="p-button-raised p-button-text" @click="open"/>
  <Dialog header="Start chat" v-model:visible="show" :modal="true" class="w-96">

    <div class="h-96 flex flex-col gap-8 py-8">
      <span class="p-float-label w-full">
        <InputText id="groupName" type="text" v-model="groupName" class="w-full"/>
        <label for="groupName">groupName</label>
      </span>
      <MultiSelect class="w-full" display="chip" v-model="members" :options="users"
                   optionLabel="username" placeholder="Select members"/>
    </div>

    <template #footer>
      <Button label="cancel" icon="pi pi-times" @click="close" class="p-button-text"/>
      <Button label="create" icon="pi pi-check" @click="startChat" autofocus/>
    </template>
  </Dialog>
</template>

<script>
import UserService from "@/api/UserService";
import ChatService from "@/api/ChatService";

export default {
  name: "CreateGroupChatDialog",
  components: {},
  data() {
    return {
      show: false,
      members: [],
      groupName: '',
    }
  },

  computed: {
    users() {
      const me = UserService.me.value;
      return UserService.users.value
          .filter(user => user.id !== me.id);
    },
  },

  methods: {
    open() {
      this.show = true;
    },
    close() {
      this.show = false;
    },

    add(user) {
      this.members.push(user);
    },

    startChat() {
      ChatService.startGroupChat(this.groupName, this.members);
      this.close();
    }
  }
}
</script>