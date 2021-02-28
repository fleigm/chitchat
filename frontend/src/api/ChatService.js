import {computed, ref} from 'vue'
import {http} from "@/httpPlugin";


const chats = ref([]);
const groupChats = computed(() => chats.value.filter(isGroupChat));
const privateChats = computed(() => chats.value.filter(isPrivateChat));

function fetchChats() {
    return http.get('chats')
        .then(({data}) => (chats.value = data));
}

function isPrivateChat(chat) {
    return Object.prototype.hasOwnProperty.call(chat, 'participantA');
}

function isGroupChat(chat) {
    return !isPrivateChat(chat);
}

function startChatWith(user) {
    return createChat({user: user.id, type: 'private'});
}

async function createChat(chat) {
    return http.post('chats', chat).then(({data}) => (chats.value.push(data)))
}


export default {
    chats,
    groupChats,
    privateChats,
    fetchChats,
    isPrivateChat,
    isGroupChat,
    startChatWith,
}