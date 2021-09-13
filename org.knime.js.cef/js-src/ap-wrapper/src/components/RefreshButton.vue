<script>
import RefreshIcon from '~/webapps-common/ui/assets/img/icons/load.svg?inline';

export default {
    components: {
        RefreshIcon
    },
    methods: {
        refresh() {
            location.reload();
        }

    }
};
</script>

<template>
  <div class="container">
    <a
      class="float"
      @click="refresh"
    >
      <p class="icon">
        <RefreshIcon />
      </p>
    </a>
  </div>
</template>

<style lang="postcss" scoped>

.container {
  position: fixed;
  width: 40px;
  height: 40px;
  bottom: 10px;
  right: 55px;
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
