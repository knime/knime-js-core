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


    <EditButton
      v-if="item.type !== 'row'"
      class="deleteButton"
      title="Delete"
      @click.prevent.stop="onContentItemDelete"
    >
      <DeleteIcon />
    </EditButton>
    <EditButton
      v-if="item.type === 'view' || item.type === 'nestedLayout' || item.type === 'quickform'"
      class="configButton"
      title="Configure"
      @click.prevent.stop="onContentItemConfigure"
    >
      <ConfigIcon />
    </EditButton>
  </div>
</template>


<script>
import KnimeView from './KnimeView';
import EditButton from './EditButton';
import DeleteIcon from 'open-iconic/svg/trash.svg';
import ConfigIcon from 'open-iconic/svg/cog.svg';

export default {
    components: {
        KnimeView,
        EditButton,
        DeleteIcon,
        ConfigIcon
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


<style lang="postcss" scoped>
.item {
  position: relative; // needed for handle positioning
  min-height: 20px;

  &:not(:last-of-type) {
    margin-bottom: 5px;
  }

  & .deleteButton {
    border-radius: 0 3px 0 0;
  }

  & .configButton {
    right: 20px;
  }
}
</style>
