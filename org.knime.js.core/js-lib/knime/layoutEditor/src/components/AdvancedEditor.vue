<template>
  <div class="debug">
    <label>
      <input
        v-model="$store.state.editMode"
        type="checkbox"
      > edit mode
    </label>
    <label>
      <input
        v-model="$store.state.selectionMode"
        type="checkbox"
      > selection
    </label>
    <button @click="onInitialLayout">initial layout</button>
    <br>
    Debug (or advanced layout)<br>
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
                this.$store.commit('setLayout', JSON.parse(e.target.value));
            } catch (e) {
                // nothing to do if JSON isn't valid
            }
        },
        onInitialLayout(e) {
            this.$store.commit('initialLayout');
        }
    }
};
</script>


<style lang="scss">
.debug {
  textarea {
    width: 100%;
    height: 90vh;
    resize: none;
    font-size: 12px;
    font-family: courier;
    overflow: auto;
    white-space: pre;
    word-break: unset;
    word-wrap: unset;
    overflow-wrap: unset;
  }
}
</style>
