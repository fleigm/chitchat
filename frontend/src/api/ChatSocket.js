import {http} from '@/httpPlugin'
import {Events, EventService} from "@/Events";

let socket = null;

async function connect() {
    const endpoint = process.env.VUE_APP_BACKEND_URL.replace("http", "ws") + "/chat"
    const response = await http.post('sockets');
    const token = response.data.id;

    socket = new WebSocket(`${endpoint}/${token}`)

    socket.onopen = (e) => {
        console.log('opened connection');
        console.log(e);
    };
    socket.onmessage = onMessage;
    socket.onerror = (e) => {
        console.log('socket error');
        console.log(e);
    }
}

function sendMessage(message) {
    socket.send(JSON.stringify(message));
}

function onMessage(event) {
    EventService.emit(Events.NEW_MESSAGE, JSON.parse(event.data));
}

export default {
    socket,
    connect,
    sendMessage,
}
