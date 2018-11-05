<template>
  <div :class="['view', resizeClass, {'missing': disabledOrMissing}]">
    <div
      v-if="node"
      :title="node.name"
      class="d-inline-flex align-items-center justify-content-center"
    >
      <main>
        <img :src="node.icon"><br>{{ node.name }}
        <small class="text-muted">Node {{ view.nodeID }}</small>
        <small
          v-if="!node.availableInView"
          class="text-muted"
        >
          (disabled in node usage)
        </small>
        <div
          v-if="node && node.description && node.description.length"
          class="description"
        >
          <small>{{ node.description }}</small>
        </div>
      </main>
    </div>

    <div v-else>
      <main>Node {{ view.nodeID }} (missing in workflow)</main>
    </div>
  </div>
</template>


<script>
export default {
    props: {
        view: { default: () => [], type: Object }
    },
    computed: {
        disabledOrMissing() {
            return !this.node || !this.node.availableInView;
        },
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

<style lang="postcss">
@import "../../style/variables.css";

/* when dragging from available nodes/elements over layout,
  this list element will temporarily be added to the layout */
.layoutPreview li.sortable-ghost {
  list-style: none;
  background-color: var(--knime-view-preview);
  border-radius: 3px;
  margin: 5px 0;
}
</style>

<style lang="postcss" scoped>
@import "../../style/variables.css";

.view {
  background-color: var(--knime-view-preview);
  border-radius: 3px;
  overflow: hidden;
  text-align: center;

  &.missing {
    opacity: 0.4;
  }

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

  & main {
    padding: 0 10px;

    & .description {
      line-height: 100%;
    }
  }
}
</style>
