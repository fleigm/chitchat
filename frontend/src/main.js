import {createApp} from 'vue'
import App from './App.vue'
import './index.css'
import keycloakPlugin, {$keycloak} from "@/keycloakPlugin";
import httpPlugin from "@/httpPlugin";
import PrimeVue from "primevue/config";
import 'primevue/resources/themes/saga-blue/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';
import Avatar from "primevue/avatar";
import Button from "primevue/button";
import ProgressSpinner from "primevue/progressspinner";


const app = createApp(App)
    .use(keycloakPlugin)
    .use(httpPlugin)
    .use(PrimeVue);

app.component('Avatar', Avatar);
app.component('Button', Button);
app.component('ProgressSpinner', ProgressSpinner);

$keycloak
    .init({
        onLoad: 'login-required',
    })
    .then((authenticated) => {
        app.mount('#app');
    });