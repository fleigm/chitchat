import Keycloak from 'keycloak-js';
import {http} from "@/httpPlugin";

const initOptions = {
    url: process.env.VUE_APP_KEYCLOAK_URL,
    realm: process.env.VUE_APP_KEYCLOAK_REALM,
    clientId: process.env.VUE_APP_KEYCLOAK_CLIENT_ID,
};

export const $keycloak = Keycloak(initOptions);

http.interceptors.request.use(async config => {
    const token = await updateToken();
    config.headers.common['Authorization'] = `Bearer ${token}`;
    return config;
});

async function updateToken() {
    await $keycloak.updateToken(70);
    return $keycloak.token;
}

export default {
    install(app, options) {
        app.config.globalProperties.$keycloak = $keycloak;
    }
}