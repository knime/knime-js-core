<template>
  <div class="item">
    <KnimeView
      v-if="item.type === 'view'"
      :view="item"
    />
    <Row
      v-else-if="item.type === 'row'"
      :row="item"
    />
    <div
      v-else-if="item.type === 'nestedLayout'"
      :row="item"
    >
      nestedLayout
    </div>
    <div
      v-else-if="item.type === 'html'"
      :row="item"
    >
      html
    </div>

    <template v-if="isDeletable">
      <div
        class="deleteHandle"
        title="Delete"
        @click.prevent.stop="onContentItemDelete"
      >
        ×
      </div>
    </template>
    <div
      v-if="item.type === 'view'"
      class="configHandle"
      title="Configure"
      @click.prevent.stop="onContentItemConfigure"
    >
      ⚒
    </div>
  </div>
</template>


<script>
import KnimeView from './KnimeView';

export default {
    components: {
        KnimeView
        // Row compontent is added dynamically in beforeCreate() method, see below
    },
    props: {
        item: { default: () => {}, type: Object }
    },
    computed: {
        isDeletable() {
            if (!this.$store.state.editMode) {
                return false;
            }

            if (this.item.type === 'row') {
                // make sure only empty rows can be deleted
                const firstColumnWithContent = this.item.columns.find(column => column.content.length > 0);
                return !firstColumnWithContent;
            } else {
                return true;
            }
        }
    },
    beforeCreate() {
        // dynamic import because of recursive components (see https://vuejs.org/v2/guide/components-edge-cases.html#Circular-References-Between-Components)
        this.$options.components.Row = require('./Row.vue').default; // eslint-disable-line
    },
    methods: {
        onContentItemDelete() {
            this.$store.commit('deleteContentItem', this.item);
        },
        onContentItemConfigure() {
            alert('not implemented yet');
        }
    }
};
</script>


<style lang="scss">
.item {
  position: relative;
  min-height: 20px;

  .configHandle {
    width: 10px;
    height: 10px;
    background-color: red;
    position: absolute;
    right: 15px;
    top: 0;
    cursor: pointer;
    color: #fff;
    line-height: 10px;
    text-align: center;
    font-size: 6px;
    z-index: 1000;

    &:hover,
    &.active {
      background-color: orange;
    }
  }
}
</style>
