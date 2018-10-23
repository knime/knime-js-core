<template>
  <div
    :class="['row', {'selected': isSelected}]"
    @click.stop="onRowClick"
  >
    <Column
      v-for="(column, index) in columns"
      :key="index"
      :resizable="columns.length > 1 && column != columns[columns.length - 1]"
      :deletable="isColumnDeletable(column)"
      :column="column"
    />

    <div
      v-if="canAddColumn"
      class="addColumnHandle"
      title="Add column"
      @click.prevent.stop="onAddColumn"
    >
      +
    </div>
  </div>
</template>


<script>
import Column from './Column';
import config from '../../config';

export default {
    components: { Column },
    props: {
        row: { default: () => {}, type: Object }
    },
    computed: {
        isSelected() {
            return this.$store.state.selectedItem === this.row;
        },
        canAddColumn() {
            return this.columns.length < config.gridSize;
        },
        columns: {
            get() {
                return this.row.columns;
            },
            set(newColumns) {
                this.$store.commit('updateColumns', { row: this.row, newColumns });
            }
        }
    },
    methods: {
        onRowClick() {
            if (this.$store.state.selectionMode) {
                this.$store.commit('setSelection', this.row);
            }
        },
        onAddColumn() {
            this.$store.commit('addColumn', this.row);
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


<style lang="scss">
.editMode {
  .row {
    border: 4px solid pink;
    min-height: 30px;

    &.selected {
      outline: 2px solid red;
    }

    .addColumnHandle {
      width: 10px;
      height: 10px;
      background-color: red;
      position: absolute;
      right: 0;
      top: 15px;
      cursor: pointer;
      color: #fff;
      line-height: 7px;
      text-align: center;
    }
  }
}
</style>
