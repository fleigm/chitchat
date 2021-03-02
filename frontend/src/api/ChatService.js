import {computed, ref} from 'vue'
import {http} from "@/httpPlugin";
import {Events, EventService} from "@/Events";


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

function startGroupChat(name, members) {
    const payload = {
        type: 'group',
        name,
        members: members.map(u => u.id),
    };
    return createChat(payload);
}

function startChatWith(user) {
    return createChat({user: user.id, type: 'private'});
}

async function createChat(chat) {
    return http.post('chats', chat)
        .then(({data}) => {
            chats.value.push(data)
            EventService.emit(Events.OPEN_CHAT, data);
        });
}

function isPartOfChat(userId, chat) {
    if (isPrivateChat(chat)) {
        return chat.participantA.id === userId || chat.participantB.id === userId;
    }
    return chat.members.some(user => user.id === userId);
}


export default {
    chats,
    groupChats,
    privateChats,
    fetchChats,
    isPrivateChat,
    isGroupChat,
    startChatWith,
    startGroupChat,
    isPartOfChat,
}