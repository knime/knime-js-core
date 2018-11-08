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

    <Popper
      v-if="item.type === 'view' || item.type === 'nestedLayout' || item.type === 'quickform'"
      trigger="click"
      :options="{placement: 'top'}"
      :append-to-body="true"
      :force-show="showConfigDialog"
      @show="showConfigDialog = true"
      @hide="showConfigDialog = false"
    >
      <EditButton
        slot="reference"
        :class="['configButton', {active: showConfigDialog}]"
        title="Configure size"
      >
        <ConfigIcon />
      </EditButton>

      <div class="popper config">
        <ConfigDialog
          v-if="showConfigDialog"
          :item="item"
          @close="showConfigDialog = false"
        />
      </div>
    </Popper>
    <button
      v-if="showConfigDialog"
      class="popperBackdrop"
      @click.self.stop.prevent="showConfigDialog = false"
      @mousedown.prevent
    />
  </div>
</template>


<script>
import KnimeView from './KnimeView';
import EditButton from './EditButton';
import Popper from 'vue-popperjs';
import ConfigDialog from './ConfigDialog';
import DeleteIcon from 'open-iconic/svg/trash.svg';
import ConfigIcon from 'open-iconic/svg/cog.svg';

export default {
    components: {
        KnimeView,
        EditButton,
        Popper,
        ConfigDialog,
        DeleteIcon,
        ConfigIcon
        // Row compontent is added dynamically in beforeCreate() method, see below
    },
    props: {
        item: { default: () => {}, type: Object }
    },
    data() {
        return {
            showConfigDialog: false
        };
    },
    beforeCreate() {
        // dynamic import because of recursive components (see https://vuejs.org/v2/guide/components-edge-cases.html#Circular-References-Between-Components)
        this.$options.components.Row = require('./Row.vue').default; // eslint-disable-line
    },
    methods: {
        onContentItemDelete() {
            this.$store.commit('deleteContentItem', this.item);
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

/* full window overlay to prevent other actions while popover is open */
.popperBackdrop {
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
  z-index: 200;
}
</style>

<style lang="postcss">
/* overwrite some global vue-popperjs styles */
.popper.config {
  color: inherit;
  text-align: left;
  background-color: #fff;
  border: none;
  padding: 0;
  font-size: inherit;
  max-width: 430px;
}
</style>
