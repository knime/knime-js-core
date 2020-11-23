<template>
  <div
    :class="['knimeView', typeClass, resizeClass, 'd-flex align-items-center justify-content-center',
             {'missing': disabledOrMissing}
    ]"
    :style="style"
  >
    <div
      v-if="node"
      :title="node.name"
      class="d-inline-flex align-items-center justify-content-center"
    >
      <main>
        <img :src="node.icon"><br>{{ node.name }}<br>
        <small class="text-muted node-id">Node&nbsp;{{ view.nodeID }}</small>
        <small
          v-if="disabledOrMissing"
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
      <main><span class="node-id">Node&nbsp;{{ view.nodeID }}</span> (missing in workflow)</main>
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
            if (this.node) {
                if (this.node.hasOwnProperty('availableInView')) {
                    return !this.node.availableInView;
                } else if (this.node.hasOwnProperty('availableInDialog')) {
                    return !this.node.availableInDialog;
                }
                return false;
            } else {
                return true;
            }
        },
        node() {
            return this.$store.state.nodes.find(node => node.nodeID === this.view.nodeID);
        },
        typeClass() {
            return this.node && this.node.type;
        },
        resizeClass() {
            if (!this.view.resizeMethod) {
                return null;
            }
            return this.view.resizeMethod;
        },
        style() {
            const styleProps = ['minWidth', 'maxWidth', 'minHeight', 'maxHeight'];

            // extract style props
            const style = {};
            styleProps.forEach(prop => {
                if (this.view.hasOwnProperty(prop)) {
                    let value = this.view[prop];
                    if (value) {
                        if (!value.toString().includes('px')) {
                            value = `${value}px`;
                        }
                        style[prop] = value;
                    }
                }
            });
            return style;
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
  width: 100%;
  height: 50px;
  color: transparent;

  & * {
    display: none; /* for now we just hide the content, maybe there is a better way to render the ghost */
  }
}
</style>

<style lang="postcss" scoped>
@import "../../style/variables.css";

.knimeView {
  background-color: var(--knime-view-preview);
  border-radius: 3px;
  overflow: hidden;
  text-align: center;
  padding: 10px;

  &.missing {
    opacity: 0.4;
  }

  &.view,
  &.nestedLayout {
    min-height: 150px;
  }

  &.sortable-drag {
    background-color: var(--knime-view-preview-semi);
  }

  &.quickform,
  &.configuration {
    background-color: var(--knime-configuration-preview);

    &.sortable-drag {
      background-color: var(--knime-configuration-preview-semi);
    }
  }

  &.aspectRatio16by9,
  &.aspectRatio4by3,
  &.aspectRatio1by1 {
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

  &.aspectRatio1by1 {
    padding-bottom: 100%;
  }

  & main {
    padding: 0 10px;

    & .description {
      line-height: 100%;
    }

    & .node-id {
      white-space: nowrap;
    }
  }
}
</style>
