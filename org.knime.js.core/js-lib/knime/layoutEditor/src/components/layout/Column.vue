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
    
    <button
      v-if="resizable"
      :class="['resizeHandle', {'active': isCurrentColumnResizing}]"
      title="Drag to resize"
      @mousedown.prevent.stop="onColumnResizeMouseDown"
    />
    <EditButton
      v-if="deletable"
      class="deleteButton"
      title="Delete column"
      @click.prevent.stop="onColumnDelete"
    >
      <DeleteIcon />
    </EditButton>
    <button
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
import EditButton from './EditButton';
import DeleteIcon from 'open-iconic/svg/trash.svg';

export default {
    components: {
        ColumnContent,
        Draggable,
        EditButton,
        DeleteIcon
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
                `col-${this.column.widthXS}`,
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

<style lang="postcss" scoped>
@import "../../style/variables.css";

.col {
  --resize-color: var(--knime-gray-ultra-light);
  --resize-color-active: var(--knime-yellow-sec-server);
  --resize-arrow-width: 4px;
  --resize-arrow-height: 4px;

  background-color: #fff;
  padding: 5px 10px 5px 5px;
  min-height: 60px;

  & .draggable {
    cursor: move; /* for IE11 */
    cursor: grab;
  }

  & .resizeHandle {
    margin: 0;
    padding: 0;
    outline: 0;
    width: 6px; /* quite thick to be easily clickable */
    height: 100%;
    background-color: var(--resize-color);
    position: absolute;
    right: 0;
    bottom: 0;
    border-style: solid;
    border-width: 10px 2px 10px 2px; /* but adding a border to reduce the line thinkness */
    border-color: #fff;

    &:before,
    &:after {
      content: "";
      width: 0;
      height: 0;
      border-style: solid;
      z-index: 1;
      display: block;
      position: absolute;
      top: calc(50% - var(--resize-arrow-height));
      left: 0px;
      pointer-events: none;
    }

    &:before {
      left: calc((var(--resize-arrow-width) * -1) - 1px);
      border-width: var(--resize-arrow-height) var(--resize-arrow-width)
        var(--resize-arrow-height) 0;
      border-color: transparent var(--resize-color) transparent transparent;
    }
    &:after {
      left: 3px;
      border-width: var(--resize-arrow-height) 0 var(--resize-arrow-height)
        var(--resize-arrow-width);
      border-color: transparent transparent transparent var(--resize-color);
    }

    &:hover,
    &.active {
      background-color: var(--resize-color-active);

      &:before {
        border-color: transparent var(--resize-color-active) transparent
          transparent;
      }
      &:after {
        border-color: transparent transparent transparent
          var(--resize-color-active);
      }
    }
  }

  & .deleteButton {
    right: 5px;
  }

  /* full window overlay while resizing to prevent loosing mouse events e.g. due to iframes in columns */
  & .resizeOverlay {
    margin: 0;
    padding: 0;
    border: 0;
    outline: 0;
    background-color: transparent;
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    z-index: 1000;
  }

  /* also we use the overlay to keep resize cursor while resizing */
  & .resizeHandle,
  & .resizeOverlay {
    cursor: col-resize;
  }
}
</style>
