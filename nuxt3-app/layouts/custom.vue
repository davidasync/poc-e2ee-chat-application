<template>
  <div class="container mx-auto p-5">
    <header class="md:flex md:flex-row md:justify-between">
      <NuxtLink to="/" class="flex flex-row justify-center">
        <h1 class="text-3xl text-gray-600 ml-2">
          Chat Room
        </h1>
      </NuxtLink>
      <div class="mt-2">
        <a href="#" class="text-gray-600 hover:text-purple-600 p-4">Home</a>
        <a href="#" class="text-gray-600 hover:text-purple-600 p-4">Blog</a>
        <a href="#" class="text-gray-600 hover:text-purple-600 p-4">Contact</a>
      </div>
    </header>
    <main>
      <slot />
    </main>
    <footer>
      <div class="border-t-2 border-gray-300 flex flex-col md:flex-row md:justify-between text-center py-5 text-sm mt-14">
        <div class="mb-4">
          <a href="#" class="mx-2.5">About</a>
          <a href="#" class="mx-2.5">Privacy Policy</a>
          <a href="#" class="mx-2.5">Terms of Services</a>
        </div>
        <p>&copy; Copyright Reserved 2022</p>
      </div>
    </footer>
  </div>
</template>

<script lang="ts">
  import { defineComponent } from 'vue'
  export default defineComponent({
    computed: {
      header() {
        return this.$slots.header
      }
    }
  })
</script>