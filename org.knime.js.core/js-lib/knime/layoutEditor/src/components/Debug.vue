<template>
  <div>
    <textarea
      :value="layoutAsString"
      @input="onTextareaInput"
    />
  </div>
</template>


<script>

export default {
    computed: {
        layoutAsString() {
            const indention = 2;
            return JSON.stringify(this.$store.state.layout, null, indention);
        }
    },
    methods: {
        onTextareaInput(e) {
            try {
                this.$store.commit('setLayoutByTextarea', JSON.parse(e.target.value));
            } catch (e) {
                // nothing to do if JSON isn't valid
            }
        }
    }
};
</script>


<style lang="postcss" scoped>
div {
  padding: 0;
  display: flex;
}
textarea {
  flex: 1;
  resize: none;
  font-size: 12px;
  font-family: courier;
  overflow: auto;
  white-space: pre;
  word-break: unset;
  word-wrap: unset;
  overflow-wrap: unset;
  outline: 0;
  border: 0;
}
</style>
