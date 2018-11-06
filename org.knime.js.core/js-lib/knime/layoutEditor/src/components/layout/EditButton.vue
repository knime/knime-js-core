<template>
  <button v-on="$listeners">
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
  background-color: var(--knime-gray-ultra-light);
  position: absolute;
  right: 0;
  top: 0;
  cursor: pointer;
  opacity: 1;
  transition: opacity 0.3s;

  &:hover {
    background-color: var(--knime-yellow-sec-server);
  }

  & > * {
    margin: auto;
    display: block;
  }
}
</style>
