import {http} from '@/httpPlugin'
import {Events, EventService} from "@/Events";

export const Types = {
    SEND_MESSAGE: 'send_message',
    INCOMING_MESSAGE: 'message',
}

let socket = null;

async function connect() {
    const endpoint = process.env.VUE_APP_BACKEND_URL.replace("http", "ws") + "/chat"
    const response = await http.post('sockets');
    const token = response.data.id;

    socket = new WebSocket(`${endpoint}/${token}`)

    socket.onopen = (e) => {
        console.log('opened connection');
    };
    socket.onmessage = onMessage;
    socket.onerror = (e) => {
        console.log('socket error');
        console.log(e);
    }
}

function sendMessage(message) {
    send(Types.SEND_MESSAGE, message);
}

function send(type, payload) {
    const webSocketMessage = {
        type,
        payload,
    };

    socket.send(JSON.stringify(webSocketMessage));
}

function onMessage(event) {
    const webSocketMessage = JSON.parse(event.data);
    
    if (webSocketMessage.type === Types.INCOMING_MESSAGE) {
        EventService.emit(Events.NEW_MESSAGE, webSocketMessage.payload);
    }
}

export default {
    socket,
    connect,
    sendMessage,
}
