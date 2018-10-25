<template>
  <Draggable
    v-model="content"
    :options="{group: 'content', draggable: '.draggable'}"
    :class="columnClasses"
    :move="onMove"
  >
    <ColumnContent
      v-for="(item, index) in content"
      :key="index"
      :item="item"
      class="draggable"
    />
    
    <div
      v-if="resizable"
      :class="['resizeHandle', {'active': isCurrentColumnResizing}]"
      title="Drag to resize"
      @mousedown.prevent.stop="onColumnResizeMouseDown"
    />
    <div
      v-if="deletable"
      class="editHandle"
      title="Delete column"
      @click.prevent.stop="onColumnDelete"
    >
      ×
    </div>
    <div
      v-if="isCurrentColumnResizing"
      class="resizeOverlay"
      @mouseup="onColumnResizeMouseUp"
      @mouseout="onColumnResizeMouseUp"
      @mousemove.stop="onColumnResizeMouseMove"
    />
  </Draggable>
</template>


<script>
import config from '../../config';
import utils from '../../utils';
import ColumnContent from './ColumnContent';
import Draggable from 'vuedraggable';

export default {
    components: {
        ColumnContent,
        Draggable
    },
    props: {
        column: { default: () => {}, type: Object },
        resizable: { default: false, type: Boolean },
        deletable: { default: false, type: Boolean }
    },
    computed: {
        columnClasses() {
            // actually we don't support responsive layouts yet; but we apply all classes in case the user modified via JSON layout editor
            return [
                'col',
                `col-xs-${this.column.widthXS}`,
                `col-sm-${this.column.widthSM}`,
                `col-md-${this.column.widthMD}`,
                `col-lg-${this.column.widthLG}`,
                `col-xl-${this.column.widthXL}`
            ];
        },
        content: {
            get() {
                return this.column.content;
            },
            set(newContent) {
                this.$store.commit('updateColumnContent', { column: this.column, newContent });
            }
        },
        isCurrentColumnResizing() {
            const info = this.$store.state.resizeColumnInfo;
            return info && info.column === this.column;
        }
    },
    methods: {
        onMove(e) {
            return utils.checkMove(e);
        },
        onColumnDelete() {
            this.$store.commit('deleteColumn', this.column);
        },
        onColumnResizeMouseDown(e) {
            const containerWidth = e.target.parentNode.parentNode.offsetWidth;
            this.$store.commit('setResizeColumnInfo', {
                column: this.column,
                clientX: e.clientX,
                gridStepWidth: containerWidth / config.gridSize,
                originalWidthMD: this.column.widthMD // currently we don't support responsive layouts, only using widthMD
            });
        },
        onColumnResizeMouseUp(e) {
            this.$store.commit('setResizeColumnInfo', null);
        },
        onColumnResizeMouseMove(e) {
            const resizeColumnInfo = this.$store.state.resizeColumnInfo;
            if (resizeColumnInfo) {
                const moveDelta = e.clientX - resizeColumnInfo.clientX;
                const gridDelta = Math.round(moveDelta / resizeColumnInfo.gridStepWidth);
                let newWidth = resizeColumnInfo.originalWidthMD + gridDelta;
                this.$store.commit('resizeColumn', newWidth);
            }
        }
    }
};
</script>


<style lang="scss" scoped>
.col {
  border: 1px solid green;
  background-color: #fff;
  min-height: 50px;

  .draggable {
    cursor: move; // for IE11
    cursor: grab;
  }

  .resizeHandle {
    width: 5px;
    height: 100%;
    background-color: green;
    position: absolute;
    right: 0;
    bottom: 0;

    &:hover,
    &.active {
      background-color: rgb(0, 184, 0);
    }
  }

  // full window overlay while resizing to prevent loosing mouse events e.g. due to iframes in columns
  .resizeOverlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    z-index: 1000;
  }

  // also we use the overlay to keep resize cursor while resizing
  .resizeHandle,
  .resizeOverlay {
    cursor: col-resize;
  }
}
</style>
