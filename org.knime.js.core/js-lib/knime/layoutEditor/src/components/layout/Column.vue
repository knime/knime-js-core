<template>
  <Draggable
    v-model="content"
    :options="{group: 'content', draggable: '.draggable'}"
    :class="[columnClasses, {resizable}, {'droppable': $store.state.dragging}]"
    :move="onMove"
    @start="$store.commit('setDragging', true)"
    @end="$store.commit('setDragging', false)"
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
            // we don't support responsive layouts yet
            return [
                'col',
                `col-${this.column.widthXS}`
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
                originalWidthXS: this.column.widthXS // currently we don't support responsive layouts, only using widthXS
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
                let newWidth = resizeColumnInfo.originalWidthXS + gridDelta;
                this.$store.commit('resizeColumn', newWidth);
            }
        }
    }
};
</script>

<style lang="postcss" scoped>
@import "../../style/variables.css";

.col {
  --resize-width: 14px;
  --resize-line-width: 2px;
  --resize-border-width: calc(
    (var(--resize-width) - var(--resize-line-width)) / 2
  );
  --resize-color: var(--knime-gray-ultra-light);
  --resize-color-active: var(--knime-yellow-sec-server);
  --resize-arrow-width: 4px;
  --resize-arrow-height: 4px;

  background-color: #fff;
  padding: calc(var(--resize-width) / 2);
  min-height: 60px;

  outline-width: 2px;
  outline-style: dashed;
  outline-offset: -4px;
  outline-color: transparent;
  transition: outline-color 0.3s;

  &.droppable {
    outline-color: var(--knime-yellow-sec-server);

    & button {
      opacity: 0;
    }
  }

  & .draggable {
    cursor: move; /* for IE11 */
    cursor: grab;
  }

  & .resizeHandle {
    margin: 0;
    padding: 0;
    outline: 0;
    width: var(--resize-width); /* quite thick to be easily clickable */
    height: 100%;
    background-color: var(--resize-color);
    position: absolute;
    right: calc(var(--resize-width) / 2 * -1);
    z-index: 100;
    bottom: 0;
    border-style: solid;
    border-width: 10px var(--resize-border-width) 10px
      var(--resize-border-width); /* but adding a border to reduce the line thinkness */
    border-color: #fff;

    opacity: 1;
    transition: opacity 0.3s;

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
      pointer-events: none;
    }

    &:before {
      left: calc((var(--resize-arrow-width) * -1) - 1px);
      border-width: var(--resize-arrow-height) var(--resize-arrow-width)
        var(--resize-arrow-height) 0;
      border-color: transparent var(--resize-color) transparent transparent;
    }
    &:after {
      right: calc((var(--resize-arrow-width) * -1) - 1px);
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

  /* align delete button to resize handle */
  &.resizable > .deleteButton {
    right: 8px;
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
