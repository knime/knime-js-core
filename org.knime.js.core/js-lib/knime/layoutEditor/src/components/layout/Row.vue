<template>
  <div
    class="row"
  >
    <Column
      v-for="(column, index) in columns"
      :key="index"
      :resizable="columns.length > 1 && column != columns[columns.length - 1]"
      :deletable="isColumnDeletable(column)"
      :column="column"
    />

    <button
      v-if="canAddColumn"
      class="editHandle addColumnHandle"
      title="Add column"
      @click.prevent.stop="onAddColumn"
    >
      +
    </button>
    <button
      v-if="isRowDeletable"
      class="editHandle"
      title="Delete row"
      @click.prevent.stop="onRowDelete"
    >
      ×
    </button>
  </div>
</template>


<script>
import Column from './Column';
import config from '../../config';

export default {
    components: { Column },
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


<style lang="scss" scoped>
.row {
  border: 4px solid pink;
  min-height: 50px;
  position: relative; // needed for delete handle positioning

  &:not(:last-of-type) {
    margin-bottom: 5px;
  }

  .addColumnHandle {
    top: 20px;
  }
}
</style>
