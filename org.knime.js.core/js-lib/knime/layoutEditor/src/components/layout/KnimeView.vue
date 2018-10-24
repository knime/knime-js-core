<template>
  <div :class="['view', resizeClass, {'missing': !node}]">
    <div
      v-if="node"
      :title="node.name"
    >
      <img :src="node.icon"> {{ node.name }}
      <small>Node {{ view.nodeID }}</small>
      <div
        v-if="node && node.description && node.description.length"
        class="description"
      >
        {{ node.description }}
      </div>
    </div>

    <div v-else>
      Node {{ view.nodeID }} (missing)
    </div>
  </div>
</template>


<script>
export default {
    props: {
        view: { default: () => [], type: Object }
    },
    computed: {
        node() {
            return this.$store.state.nodes.find(node => node.nodeID === this.view.nodeID);
        },
        resizeClass() {
            if (!this.view.resizeMethod) {
                return null;
            }
            return this.view.resizeMethod;
        }
    }
};
</script>


<style lang="scss">
.editMode {
  .view {
    border: 1px solid black;

    &.missing {
      opacity: 0.4;
    }
  }
}

.view {
  overflow: hidden;

  &.aspectRatio16by9,
  &.aspectRatio4by3 {
    position: relative;
    width: 100%;
    height: 0;

    & > :first-child {
      position: absolute;
      width: 100%;
      height: 100%;
      left: 0;
      top: 0;
    }
  }

  &.aspectRatio16by9 {
    padding-bottom: calc(100% / (16 / 9));
  }

  &.aspectRatio4by3 {
    padding-bottom: calc(100% / (4 / 3));
  }
}
</style>
