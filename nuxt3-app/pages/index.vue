<template>
  <NuxtLayout name="custom">
    <div class="mt-10">
      <input type="text" v-model="usernameNewRoom" placeholder="input username"/>
      <button class="btn rounded-lg bg-blue-500 ml-2" v-on:click="createRoomChat()">
        <div class="p-2">Create Chat Room</div>
      </button>
    </div>
    <div class="mt-10">
      <input type="text" v-model="usernameToJoin" placeholder="input username"/>
      <input type="text" v-model="roomId" placeholder="input room id"/>
      <button class="btn rounded-lg bg-blue-500 ml-2" v-on:click="joinRoomChat()">
        <div class="p-2">Join Chat Room</div>
      </button>
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
        usernameNewRoom: '',
        usernameToJoin: '',
        roomId: '',
      }
    },
    methods: {
      async joinRoomChat() {
        await $fetch('http://localhost:8080/join', {
          method: 'POST',
          body: {
            roomId: this.roomId,
            username: this.usernameToJoin,
            rsaPublicKey: window.localStorage.getItem(Asymmetrical.RSA_PUBLIC_KEY),
          },
        })

        this.$router.push(`/${this.roomId}`)
      },
      async createRoomChat() {
        const response = await $fetch('http://localhost:8080/', {
          method: 'POST',
          body: {
            username: this.usernameNewRoom,
            rsaPublicKey: window.localStorage.getItem(Asymmetrical.RSA_PUBLIC_KEY),
          },
        })

        const secretKey = new Symmetrical(response.id);
        const encryptedSecretKey = Asymmetrical.encrypt(
          window.localStorage.getItem(Asymmetrical.RSA_PUBLIC_KEY),
          secretKey
        )

        await $fetch('http://localhost:8080/', {
          method: 'PATCH',
          body: {
            username: this.usernameNewRoom,
            rsaPublicKey: window.localStorage.getItem(Asymmetrical.RSA_PUBLIC_KEY),
            chatRoomId: response.id,
            chatRoomMemberId: response.roomMaster.id,
            encryptedSecretKey: encryptedSecretKey,
          },
        })

        this.$router.push(`/${response.id}`)
      }
    }
  })
</script>
