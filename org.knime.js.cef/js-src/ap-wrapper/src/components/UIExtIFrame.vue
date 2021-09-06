<script>

export default {
    props: {
        node: {
            required: true,
            type: Object
        }
    },
    computed: {
        src() {
            return '';
        }
    }
};
</script>

<template>
  <iframe
    v-if="src"
    :src="src"
  />
</template>

<style lang="postcss">
</style>
