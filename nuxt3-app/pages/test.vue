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
      new Symmetrical()
      this.symEncryptedSampleText1 = await Symmetrical.encrypt(this.sampleText)
      this.symDecryptedSampleText1 = await Symmetrical.decrypt(this.symEncryptedSampleText1)

      const df = await DeffieHellman.create()
      this.dfEncryptedSampleText1 = await df.encrypt(this.sampleText)
      this.dfDecryptedSampleText1 = await df.decrypt(this.dfEncryptedSampleText1)
    },
  })
</script>