<template>
  <div class="row no-gutters">
    <Column
      v-for="(column, index) in columns"
      :key="index"
      :resizable="columns.length > 1 && column != columns[columns.length - 1]"
      :deletable="isColumnDeletable(column)"
      :column="column"
    />
    <div
      v-if="row.pageBreakAfter"
      class="print-item"
    >
      <span class="label">Page Break</span>
    </div>

    <EditButton
      v-if="canAddColumn"
      class="addColumnButton"
      title="Add column"
      @click.prevent.stop="onAddColumn"
    >
      <AddIcon />
    </EditButton>
    <EditButton
      v-if="isRowDeletable"
      title="Delete row"
      @click.prevent.stop="onRowDelete"
    >
      <DeleteIcon />
    </EditButton>
  </div>
</template>

<script>
import Column from './Column';
import config from '../../config';
import EditButton from './EditButton';
import AddIcon from 'open-iconic/svg/plus.svg';
import DeleteIcon from 'open-iconic/svg/trash.svg';

export default {
    components: { Column, EditButton, AddIcon, DeleteIcon },
    props: {
        row: { default: () => {}, type: Object },
        deletable: { default: true, type: Boolean } // only used to prevent deleting the last row in the layout
    },
    computed: {
        canAddColumn() {
            return (
                !(this.row.pageBreakAfter && this.columns.length === 0) &&
        this.columns.length < config.gridSize
            );
        },
        isRowDeletable() {
            // make sure only empty rows (= 1 empty column) can be deleted
            const isEmpty =
        this.columns.length === 0 ||
        (this.columns.length === 1 && this.columns[0].content.length === 0);
            return this.deletable && isEmpty;
        },
        columns() {
            return this.row.columns ?? [];
        }
    },
    methods: {
        onAddColumn() {
            this.$store.commit('addColumn', this.row);
        },
        onRowDelete() {
            this.$store.commit('deleteContentItem', this.row);
        },
        isColumnDeletable(column) {
            // make sure the only column in a row can't be deleted
            if (this.columns.length === 1) {
                return false;
            }

            // make sure only empty columns can be deleted
            return column.content.length === 0;
        }
    }
};
</script>

<style lang="postcss">
@import "../../style/variables.css";

/* when dragging from available elements over layout,
  this list element will temporarily be added to the layout */
.layoutPreview li.sortable-ghost.row {
  border: 4px solid var(--knime-gray-ultra-light);
  background-color: transparent;
  min-height: 68px;
}
</style>

<style lang="postcss" scoped>
@import "../../style/variables.css";

.row {
  border: 4px solid var(--knime-gray-ultra-light);
  border-radius: 3px;
  position: relative; /* needed for delete handle positioning */
  cursor: move; /* for IE11 */
  cursor: grab;

  &:not(:last-of-type) {
    margin-bottom: 5px;
  }

  & .addColumnButton {
    top: calc((var(--column-min-height) / 2) - var(--button-size) / 2);
  }

  & .print-item {
    width: 100%;
    height: 11px;
    margin: 2px 7px 11px;
    text-align: center;
    color: var(--knime-silver-sand);
    border-bottom: 2px dotted var(--knime-gray-ultra-light);

    & .label {
      padding: 0 5px;
      background-color: #ffffff;
      line-height: 18px;
      vertical-align: middle;
      white-space: nowrap;
    }
  }
}
</style>
