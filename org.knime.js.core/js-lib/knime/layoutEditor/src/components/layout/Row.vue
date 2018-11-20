<template>
  <div
    class="row no-gutters"
  >
    <Column
      v-for="(column, index) in columns"
      :key="index"
      :resizable="columns.length > 1 && column != columns[columns.length - 1]"
      :deletable="isColumnDeletable(column)"
      :column="column"
    />

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
            return this.columns.length < config.gridSize;
        },
        isRowDeletable() {
            // make sure only empty rows (= 1 empty column) can be deleted
            const isEmpty = this.columns.length === 1 && this.columns[0].content.length === 0;
            return this.deletable && isEmpty;
        },
        columns() {
            return this.row.columns;
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
}
</style>
