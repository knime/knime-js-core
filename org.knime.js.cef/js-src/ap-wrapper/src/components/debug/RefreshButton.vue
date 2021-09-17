<script>
import FunctionButton from '~/webapps-common/ui/components/FunctionButton';
import RefreshIcon from '~/webapps-common/ui/assets/img/icons/load.svg?inline';

export default {
    components: {
        FunctionButton,
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
  <FunctionButton
    primary
    class="button"
    title="Refresh"
    @click="refresh"
  >
    <RefreshIcon />
  </FunctionButton>
</template>

<style lang="postcss" scoped>
.button {
  position: fixed;
  bottom: 10px;
  right: 50px;
}
</style>

