import axios from "axios/index";

export const http = axios.create( {
    baseURL: process.env.VUE_APP_BACKEND_URL,
    crossdomain: true,
});

export default {
    install(app, options = {}) {
        console.log('install http plugin');

        app.config.globalProperties.$http = http;
    }
}