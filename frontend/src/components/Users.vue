

<script>
import {computed} from "vue";
import UserService from "@/api/UserService";

export default {
  name: "Users",

  setup() {
    const me = UserService.me.value;
    const users = computed(() => UserService.users.value.filter(user => user.id !== me.id))
    return {
      users,
    }
  },

  render() {
    return this.$slots.default({
      users: this.users,
    });
  }
}
</script>