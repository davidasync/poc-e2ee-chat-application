<template>
  <NuxtLayout name="custom">
    <div class="mt-10">
      <p>WELCOME TO ROOM {{ $route.params.chatRoomId }}</p>
      <p>MEMBERLIST = {{ chatRoomMembers }}</p>
    </div>

  </NuxtLayout>
</template>

<script lang="ts">
  import { defineComponent } from 'vue'
  import Asymmetrical from '../lib/Asymmetrical'
  import Symmetrical from '../lib/Symmetrical'

  export default defineComponent({
    layout: 'custom',
    data() {
      return {
        chatRoomMembers: [],
      }
    },
    async mounted() {
      const route = useRoute()

      const response = await $fetch(`http://localhost:8080/${route.params.chatRoomId}`, {
        method: 'GET',
      });

      console.log("awdaw")
      this.chatRoomMembers = response.chatRoomMembers.map(v => v.username);
    }
  })
</script>
