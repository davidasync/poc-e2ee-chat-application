<template>
  <NuxtLayout name="custom">
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
      const symmetrical = await Symmetrical.create()
      const df = await DeffieHellman.create()

      this.symEncryptedSampleText1 = await symmetrical.encrypt(this.sampleText)
      this.symDecryptedSampleText1 = await symmetrical.decrypt(this.symEncryptedSampleText1)

      this.dfEncryptedSampleText1 = await df.encrypt(this.sampleText)
      this.dfDecryptedSampleText1 = await df.decrypt(this.dfEncryptedSampleText1)
    },
  })
</script>