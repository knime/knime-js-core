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
  width: 14px;
  height: 14px;
  background-color: var(--knime-gray-ultra-light);
  position: absolute;
  right: 0;
  top: 0;
  cursor: pointer;

  &:hover {
    background-color: var(--knime-yellow-sec-server);
  }

  & > * {
    margin: auto;
    display: block;
  }
}
</style>
