<template>
  <div :class="['view', resizeClass]">
    <template v-if="$store.state.livePreview">
      <iframe src="knime-view1/debug.html" />
    </template>
    <template v-else>
      <div :title="node.name">
        <img :src="node.icon"> {{ node.name }}
        <small>Node {{ view.nodeID }}</small>
        <div
          v-if="node.description && node.description.length"
          class="description"
        >
          {{ node.description }}
        </div>
      </div>
    </template>
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

  iframe {
    border: 0;
  }
}
</style>
