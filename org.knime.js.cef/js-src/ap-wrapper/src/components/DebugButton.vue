<script>
import InspectorIcon from '~/webapps-common/ui/assets/img/icons/code-html.svg?inline';

export default {
    components: {
        InspectorIcon
    }
};
</script>

<template>
  <div class="container">
    <a
      href="http://localhost:8888"
      target="_blank"
      class="float"
    >
      <p class="icon">
        <InspectorIcon />
      </p>
    </a>
  </div>
</template>

<style lang="postcss">

.container {
  position: fixed;
  width: 40px;
  height: 40px;
  bottom: 10px;
  right: 10px;
  display: flex;
  background-color: rgb(238, 250, 255);
  color: rgb(0, 0, 0);
  border-radius: 50px;
  box-shadow: 1px 1px 3px rgb(153, 153, 153);
  justify-content: center;
  align-items: center;
}

.icon {
  width: 25px;
  height: 25px;
}
</style>
