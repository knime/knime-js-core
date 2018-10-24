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
    
    <template
      v-if="$store.state.editMode"
    >
      <div
        v-if="resizable"
        :class="['resizeHandle', {'active': isCurrentColumnResizing}]"
        title="Drag to resize"
        @mousedown.prevent.stop="onColumnResizeMouseDown"
      />
      <div
        v-if="deletable"
        class="deleteHandle"
        title="Delete column"
        @click.prevent.stop="onColumnDelete"
      >
        ×
      </div>
      <div
        v-if="isCurrentColumnResizing"
        class="resizeOverlay"
        @mouseup="onColumnResizeMouseUp"
        @mousemove.stop="onColumnResizeMouseMove"
      />
    </template>
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
                this.$store.commit('updateContent', { column: this.column, newContent });
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
            this.$store.commit('setResizeColumnInfo', {
                column: this.column,
                clientX: e.clientX,
                containerWidth: e.target.parentNode.parentNode.offsetWidth,
                originalWidthMD: this.column.widthMD // currently we don't support responsive layouts
            });
        },
        onColumnResizeMouseUp(e) {
            this.$store.commit('setResizeColumnInfo', null);
        },
        onColumnResizeMouseMove(e) {
            const resizeColumnInfo = this.$store.state.resizeColumnInfo;
            if (resizeColumnInfo) {
                const gridSize = config.gridSize;
                const gridStepWidth = resizeColumnInfo.containerWidth / gridSize;
                const moveDelta = e.clientX - resizeColumnInfo.clientX;
                const gridDelta = Math.round(moveDelta / gridStepWidth);
                let newWidth = resizeColumnInfo.originalWidthMD + gridDelta;
                if (newWidth <= 0) {
                    newWidth = 1;
                } else if (newWidth > gridSize) {
                    newWidth = gridSize;
                }

                this.$store.commit('resizeColumn', {
                    column: this.column,
                    newWidth
                });
            }
        }
    }
};
</script>


<style lang="scss">
.editMode {
  .row {
    .col {
      border: 1px solid green;
      background-color: #fff;
      min-height: 50px;

      &.selected {
        outline: 2px solid yellow;
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

      /* full window overlay while resizing to prevent loosing mouse events e.g. due to iframes in columns */
      .resizeOverlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        z-index: 1000;
      }

      /* also we use the overlay to keep resize cursor while resizing */
      .resizeHandle,
      .resizeOverlay {
        cursor: col-resize;
      }
    }
  }
}
</style>
