<template>
  <NuxtLayout name="custom">
    <p>sampleText: {{ sampleText }}</p>
    <p>symEncryptedSampleText1: {{ symEncryptedSampleText1 }}</p>
    <p>symDecryptedSampleText1: {{ symDecryptedSampleText1 }}</p>
    <p>dfEncryptedSampleText1: {{ dfEncryptedSampleText1 }}</p>
    <p>dfDecryptedSampleText1: {{ dfDecryptedSampleText1 }}</p>
  </NuxtLayout>
</template>

<script lang="ts">
  import { defineComponent } from 'vue'
  import Symmetrical from '../lib/Symmetrical'
  import DeffieHellman from '../lib/DeffieHellman'

  export default defineComponent({
    layout: 'custom',
    data() {
      return {
        // username: '',
        sampleText: 'This is a secure message from Mary',
        symEncryptedSampleText1: '',
        symDecryptedSampleText1: '',
        dfEncryptedSampleText1: '',
        dfDecryptedSampleText1: '',
      }
    },
    async mounted() {
      const roomId = '1'
      new Symmetrical(roomId)
      this.symEncryptedSampleText1 = await Symmetrical.encrypt(roomId, this.sampleText)
      this.symDecryptedSampleText1 = await Symmetrical.decrypt(roomId, this.symEncryptedSampleText1)

      console.log('publicKeyJwk', JSON.parse(window.localStorage.getItem(DeffieHellman.PUBLIC_KEY_JWK)))
      console.log('privateKeyJwk', JSON.parse(window.localStorage.getItem(DeffieHellman.PRIVATE_KEY_JWK)))

      await DeffieHellman.create()
      this.dfEncryptedSampleText1 = await DeffieHellman.encrypt(
        JSON.parse(window.localStorage.getItem(DeffieHellman.PUBLIC_KEY_JWK)),
        JSON.parse(window.localStorage.getItem(DeffieHellman.PRIVATE_KEY_JWK)),
        this.sampleText
      )
      this.dfDecryptedSampleText1 = await DeffieHellman.decrypt(
        JSON.parse(window.localStorage.getItem(DeffieHellman.PUBLIC_KEY_JWK)),
        JSON.parse(window.localStorage.getItem(DeffieHellman.PRIVATE_KEY_JWK)),
        this.dfEncryptedSampleText1
      )
    },
  })
</script>