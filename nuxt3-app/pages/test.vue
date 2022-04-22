<template>
  <NuxtLayout name="custom">
    <p>sampleText: {{ sampleText }}</p>
    <p>symEncryptedSampleText1: {{ symEncryptedSampleText1 }}</p>
    <p>symDecryptedSampleText1: {{ symDecryptedSampleText1 }}</p>
    <p>rsaEncryptedSampleText1: {{ rsaEncryptedSampleText1 }}</p>
    <p>rsaDecryptedSampleText1: {{ rsaDecryptedSampleText1 }}</p>
  </NuxtLayout>
</template>

<script lang="ts">
  import { defineComponent } from 'vue'
  import Symmetrical from '../lib/Symmetrical'
  import Asymmetrical from '../lib/Asymmetrical'
  export default defineComponent({
    layout: 'custom',
    data() {
      return {
        // username: '',
        sampleText: 'This is a secure message from Mary',

        symEncryptedSampleText1: '',
        symDecryptedSampleText1: '',

        rsaEncryptedSampleText1: '',
        rsaDecryptedSampleText1: '',
      }
    },
    async mounted() {
      new Symmetrical(1)
      this.symEncryptedSampleText1 = await Symmetrical.encrypt(1, this.sampleText)
      this.symDecryptedSampleText1 = await Symmetrical.decrypt(1, this.symEncryptedSampleText1)
      
      new Asymmetrical()
      this.rsaEncryptedSampleText1 = Asymmetrical.encrypt(
        window.localStorage.getItem(Asymmetrical.RSA_PUBLIC_KEY),
        this.sampleText,
      )

      this.rsaDecryptedSampleText1 = Asymmetrical.decrypt(
        window.localStorage.getItem(Asymmetrical.RSA_PRIVATE_KEY),
        this.rsaEncryptedSampleText1,
      )
    }
  })
</script>