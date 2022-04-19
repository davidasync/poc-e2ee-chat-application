<template>
  <NuxtLayout name="custom">
    <div class="mt-10">
      <input type="text" v-model="username" placeholder="input username"/>
      <button class="btn rounded-lg bg-blue-500 ml-2" v-on:click="createRoom()">
        <div class="p-2">Create Chat Room</div>
      </button>
    </div>

  </NuxtLayout>
</template>

<script lang="ts">
  import { defineComponent } from 'vue'

  export default defineComponent({
    layout: 'custom',
    data() {
      return {
        username: '',
      }
    },
    async mounted() {
      // const keyPair = await generateKey()
      // this.publicKeyJwk = keyPair.publicKeyJwk
      // this.publicKeyJwkBase64 = btoa(JSON.stringify(this.publicKeyJwk))
      // this.privateKeyJwk = keyPair.privateKeyJwk
      // this.deriveKey = await deriveKey(this.publicKeyJwk, this.privateKeyJwk)

      // this.encryptedSampleText = await encrypt(this.sampleText, this.deriveKey)
      // this.decryptedSampleText = await decrypt(this.encryptedSampleText, this.deriveKey)
    },
    methods: {
      async createRoom() {
        // const keyPair = await generateKey()
        // const requestPayload = {
        //   username: this.username,
        //   encryptedSymmetricKey: btoa(JSON.stringify(keyPair.publicKeyJwk))
        // }

        // await $fetch('http://localhost:8080/')
      }
    }
  })
</script>