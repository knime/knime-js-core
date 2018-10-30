<template>
  <div class="item">
    <KnimeView
      v-if="item.type === 'view' || item.type === 'nestedLayout' || item.type === 'quickform'"
      :view="item"
    />
    <Row
      v-else-if="item.type === 'row'"
      :row="item"
    />
    <div
      v-else-if="item.type === 'html'"
    >
      HTML
    </div>


    <button
      v-if="item.type !== 'row'"
      class="editHandle"
      title="Delete"
      @click.prevent.stop="onContentItemDelete"
    >
      ×
    </button>
    <button
      v-if="item.type === 'view' || item.type === 'nestedLayout' || item.type === 'quickform'"
      class="editHandle configHandle"
      title="Configure"
      @click.prevent.stop="onContentItemConfigure"
    >
      ⚒
    </button>
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

  &:not(:last-of-type) {
    margin-bottom: 5px;
  }

  .configHandle {
    right: 20px;
    line-height: 15px;
    font-size: 10px;
  }
}
</style>
