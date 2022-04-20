<template>
  <NuxtLayout name="custom">
    <div class="mt-10">
      <input type="text" v-model="usernameNewRoom" placeholder="input username"/>
      <button class="btn rounded-lg bg-blue-500 ml-2" v-on:click="createRoom()">
        <div class="p-2">Create Chat Room</div>
      </button>
    </div>
    <div class="mt-10">
      <input type="text" v-model="usernameToJoin" placeholder="input username"/>
      <input type="text" v-model="roomId" placeholder="input room id"/>
      <button class="btn rounded-lg bg-blue-500 ml-2" v-on:click="createRoom()">
        <div class="p-2">Join Chat Room</div>
      </button>
    </div>


  </NuxtLayout>
</template>

<script lang="ts">
  import { defineComponent } from 'vue'
  import Symmetrical from '../lib/Symmetrical'

  export default defineComponent({
    layout: 'custom',
    data() {
      return {
        usernameNewRoom: '',
        usernameToJoin: '',
        roomId: '',
      }
    },
    methods: {
      async createRoom() {
        const response = await $fetch('http://localhost:8080/', {
          method: 'POST',
          body: {
            username: this.usernameNewRoom
          },
        })

        new Symmetrical(response.id)

        this.$router.push(`/${response.id}`)
      }
    }
  })
</script>
