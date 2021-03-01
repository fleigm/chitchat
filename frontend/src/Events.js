import mitt from "mitt";

export const Events = {
    CREATE_PRIVATE_CHAT: 'chat:create_private_chat',
    OPEN_CHAT: 'chat:open',
    NEW_MESSAGE: 'chat:new_message',
}

export const EventService = mitt();

export const EventPlugin = {
    install(app) {
        app.config.globalProperties.$eventService = EventService;
    }
}