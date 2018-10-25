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


    <div
      v-if="item.type !== 'row'"
      class="editHandle"
      title="Delete"
      @click.prevent.stop="onContentItemDelete"
    >
      ×
    </div>
    <div
      v-if="item.type === 'view'"
      class="editHandle configHandle"
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


<style lang="scss" scoped>
.item {
  position: relative; // needed for handle positioning
  min-height: 20px;

  .configHandle {
    right: 15px;
    line-height: 10px;
    font-size: 6px;
  }
}
</style>
