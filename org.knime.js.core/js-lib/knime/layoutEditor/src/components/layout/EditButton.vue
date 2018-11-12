<template>
  <button
    :class="{hidden: $store.state.dragging}"
    v-on="$listeners"
  >
    <slot />
  </button>
</template>


<script>
export default {
};
</script>


<style lang="postcss" scoped>
@import "../../style/variables.css";

button {
  margin: 0;
  padding: 0;
  border: 0;
  outline: 0;
  width: var(--button-size);
  height: var(--button-size);
  background-color: var(--button-color);
  position: absolute;
  right: 0;
  top: 0;
  cursor: pointer;
  opacity: 1;
  transition: opacity 0.2s;
  &:hover,
  &.active {
    background-color: var(--button-color-highlight);
  }

  &.hidden {
    opacity: 0;
  }

  & > * {
    margin: auto;
    display: block;
  }
}
</style>
