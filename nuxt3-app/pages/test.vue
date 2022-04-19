<template>
  <NuxtLayout name="custom">
    <p>publicKeyJwk: {{ publicKeyJwk }}</p>
    <p>publicKeyJwkBase64: {{ publicKeyJwkBase64 }}</p>
    <p>privateKeyJwk: {{ privateKeyJwk }}</p>
    <p class="mt-5">sampleText: {{ sampleText }}</p>
    <p>encryptedSampleText: {{ encryptedSampleText }}</p>
    <p>decryptedSampleText: {{ decryptedSampleText }}</p>

    <div class="border-t-2 border-gray-300 flex flex-col md:flex-row md:justify-between text-center text-sm my-10" />

    <p>publicKeyJwk1: {{ publicKeyJwk1 }}</p>
    <p>publicKeyJwkBase641: {{ publicKeyJwkBase641 }}</p>
    <p>privateKeyJwk1: {{ privateKeyJwk1 }}</p>
    <p class="mt-5">sample1: {{ encryptedSampleText1 }}</p>
    <p>decryptedSampleText1: {{ decryptedSampleText1 }}</p>
    <!-- <div class="mt-20">
      <input type="text" v-model="username" placeholder="input username"/>
      <button class="btn rounded-lg bg-blue-500 ml-2" v-on:click="createRoom()">
        <div class="p-2">Create Chat Room</div>
      </button>
    </div> -->

    <p>symEncryptedSampleText1: {{ symEncryptedSampleText1 }}</p>
    <p>symDecryptedSampleText1: {{ symDecryptedSampleText1 }}</p>
  </NuxtLayout>
</template>

<script lang="ts">
  import { defineComponent } from 'vue'
  import generateKey from '../lib/generateKeyPair'
  import encrypt from '../lib/encrypt'
  import decrypt from '../lib/decrypt'
  import deriveKey from '../lib/deriveKey'
  import Symmetrical from '../lib/Symmetrical'

  export default defineComponent({
    layout: 'custom',
    data() {
      return {
        // username: '',
        sampleText: 'This is a secure message from Mary',
        encryptedSampleText: '',
        decryptedSampleText: '',
        deriveKey: '',
        publicKeyJwk: '',
        privateKeyJwk: '',
        publicKeyJwkBase64: '',

        encryptedSampleText1: '',
        decryptedSampleText1: '',
        deriveKey1: '',
        publicKeyJwk1: '',
        privateKeyJwk1: '',
        publicKeyJwkBase641: '',
        symEncryptedSampleText1: '',
        symDecryptedSampleText1: '',
      }
    },
    async mounted() {
      const keyPair = await generateKey()
      this.publicKeyJwk = keyPair.publicKeyJwk
      this.publicKeyJwkBase64 = btoa(JSON.stringify(this.publicKeyJwk))
      this.privateKeyJwk = keyPair.privateKeyJwk

      const keyPair1 = await generateKey()
      this.publicKeyJwk1 = keyPair1.publicKeyJwk
      this.publicKeyJwkBase641 = btoa(JSON.stringify(this.publicKeyJwk1))
      this.privateKeyJwk1 = keyPair1.privateKeyJwk

      this.deriveKey = await deriveKey(this.publicKeyJwk1, this.privateKeyJwk)
      this.deriveKey1 = await deriveKey(this.publicKeyJwk, this.privateKeyJwk1)

      this.encryptedSampleText = await encrypt(this.sampleText, this.deriveKey)
      this.decryptedSampleText = await decrypt(this.encryptedSampleText, this.deriveKey)

      this.encryptedSampleText1 = await encrypt(this.sampleText, this.deriveKey1)
      this.decryptedSampleText1 = await decrypt(this.encryptedSampleText, this.deriveKey1)

      const symmetrical = await Symmetrical.create()
      this.symEncryptedSampleText1 = await symmetrical.encrypt(this.sampleText)
      this.symDecryptedSampleText1 = await symmetrical.decrypt(this.symEncryptedSampleText1)
    },
    // methods: {
    //   async createRoom() {
    //     const keyPair = await generateKey()
    //     const requestPayload = {
    //       username: this.username,
    //       base64publicKeyJwk: 
    //     }

    //     await $fetch('localhost')
    //   }
    // }
  })
</script>