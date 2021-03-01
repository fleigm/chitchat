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
import {EventPlugin} from "@/Events";
import Textarea from "primevue/textarea";
import Card from "primevue/card";
import InputText from "primevue/inputtext";


const app = createApp(App)
    .use(keycloakPlugin)
    .use(httpPlugin)
    .use(EventPlugin)
    .use(PrimeVue);

app.component('Avatar', Avatar);
app.component('Button', Button);
app.component('ProgressSpinner', ProgressSpinner);
app.component('Textarea', Textarea);
app.component('InputText', InputText);
app.component('Card', Card);

$keycloak
    .init({
        onLoad: 'login-required',
    })
    .then((authenticated) => {
        app.mount('#app');
    });