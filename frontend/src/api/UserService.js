import {ref} from 'vue'
import {http} from "@/httpPlugin";

const users = ref([]);
const me = ref({});

function fetchUsers() {
    return http.get('users').then(({data}) => (users.value = data))
}

function fetchMe() {
    //$keycloak.loadUserProfile().then(profile => (me.value = profile));
    return http.get('users/me').then(({data}) => me.value = data);
}

export default {
    me,
    users,
    fetchUsers,
    fetchMe,
}
