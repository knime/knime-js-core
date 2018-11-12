<template>
  <div class="configDialog modal-body container">
    <div class="row">
      <div class="col col-auto">
        <div
          class="btn-group btn-group-sm btn-group-toggle"
        >
          <label :class="['btn', 'btn-light', {active: resizeMode === 'aspectRatio'}]">
            <input
              v-model="resizeMode"
              type="radio"
              autocomplete="off"
              value="aspectRatio"
            > Aspect ratio
          </label>
          <label :class="['btn', 'btn-light', {active: resizeMode === 'auto'}]">
            <input
              v-model="resizeMode"
              type="radio"
              autocomplete="off"
              value="auto"
            > Auto
          </label>
        </div>
      </div>
      <div class="col">
        <div
          v-if="resizeMode === 'aspectRatio'"
          class="btn-group btn-group-sm btn-group-toggle"
        >
          <label :class="['btn', 'btn-light', {active: itemConfig.resizeMethod === 'aspectRatio16by9'}]">
            <input
              v-model="itemConfig.resizeMethod"
              type="radio"
              autocomplete="off"
              value="aspectRatio16by9"
            > 16:9
          </label>
          <label :class="['btn', 'btn-light', {active: itemConfig.resizeMethod === 'aspectRatio4by3'}]">
            <input
              v-model="itemConfig.resizeMethod"
              type="radio"
              autocomplete="off"
              value="aspectRatio4by3"
            > 4:3
          </label>
          <label :class="['btn', 'btn-light', {active: itemConfig.resizeMethod === 'aspectRatio1by1'}]">
            <input
              v-model="itemConfig.resizeMethod"
              type="radio"
              autocomplete="off"
              value="aspectRatio1by1"
            > square
          </label>
        </div>

        <small
          v-if="resizeMode === 'auto'"
          class="form-row text-muted"
        >
          Height dynamically calculated depending on content.
        </small>
      </div>
    </div>

    <template v-if="resizeMode === 'auto'">
      <div class="form-row mt-2">
        <label class="col offset-2 col-form-label-sm pb-0">Width</label>
        <label class="col col-form-label-sm pb-0">Height</label>
      </div>
      <div class="form-row mb-2">
        <label class="col-2 col-form-label col-form-label-sm">Min</label>
        <div class="col">
          <div class="input-group input-group-sm">
            <input
              v-model.number="itemConfig.minWidth"
              type="number"
              step="1"
              min="0"
              class="form-control"
            >
            <div class="input-group-append">
              <span class="input-group-text">px</span>
            </div>
          </div>
        </div>
        <div class="col input-group input-group-sm">
          <input
            v-model.number="itemConfig.minHeight"
            type="number"
            step="1"
            min="0"
            class="form-control"
          >
          <div class="input-group-append">
            <span class="input-group-text">px</span>
          </div>
        </div>
      </div>
      <div class="form-row">
        <label class="col-2 col-form-label col-form-label-sm">Max</label>
        <div class="col input-group input-group-sm">
          <input
            v-model.number="itemConfig.maxWidth"
            type="number"
            step="1"
            min="0"
            class="form-control"
          >
          <div class="input-group-append">
            <span class="input-group-text">px</span>
          </div>
        </div>
        <div class="col input-group input-group-sm">
          <input
            v-model.number="itemConfig.maxHeight"
            type="number"
            step="1"
            min="0"
            class="form-control"
          >
          <div class="input-group-append">
            <span class="input-group-text">px</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>


<script>
export default {
    props: {
        item: { default: () => {}, type: Object }
    },
    data() {
        // copy config values from item
        const itemConfig = {
            resizeMethod: this.item.resizeMethod,
            minWidth: this.item.minWidth,
            maxWidth: this.item.maxWidth,
            minHeight: this.item.minHeight,
            maxHeight: this.item.maxHeight
        };
        return {
            itemConfig,
            resizeMode: itemConfig.resizeMethod.includes('aspectRatio') ? 'aspectRatio' : 'auto'
        };
    },
    watch: {
        resizeMode(resizeMode) {
            switch (resizeMode) {
            case 'aspectRatio':
                this.itemConfig.resizeMethod = 'aspectRatio16by9';

                // aspect ratio currently doesn't support min/max sizes
                this.itemConfig.minWidth = null;
                this.itemConfig.maxWidth = null;
                this.itemConfig.minHeight = null;
                this.itemConfig.maxHeight = null;
                
                break;
            case 'auto':
                this.itemConfig.resizeMethod = 'auto';
                break;
            }

            // hack to fix popper positioning
            this.$parent.updatePopper();
        },
        itemConfig: {
            handler() {
                // immediately commit changes so the user sees the result
                this.$store.commit('updateContentItemConfig', { item: this.item, config: this.itemConfig });
            },
            deep: true
        }
    },
    mounted() {
        window.addEventListener('keyup', this.onKeyUp);
    },
    beforeDestroy() {
        window.removeEventListener('keyup', this.onKeyUp);
    },
    methods: {
        onKeyUp(e) {
            if (e.key === 'Escape') {
                this.$emit('close');
            }
        }
    }
};
</script>

