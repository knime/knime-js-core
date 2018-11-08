<template>
  <div class="configDialog modal-body container">
    <div class="row">
      <div class="col col-auto">
        <div
          class="btn-group btn-group-toggle mb-3"
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
          class="btn-group btn-group-toggle"
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
          class="form-row mb-2 text-muted"
        >
          The height will be dynamically calculated depending on the content (if supported).
        </small>
      </div>
    </div>


    <div class="form-row mb-2">
      <div class="input-group">
        <div class="input-group-prepend">
          <span class="input-group-text bg-transparent border-0">Minimal</span>
        </div>
        <input
          v-model.number="itemConfig.minWidth"
          type="number"
          step="1"
          min="0"
          class="form-control"
          placeholder="width"
          title="width"
          @mousedown.stop=""
        >
        <div class="input-group-append">
          <span class="input-group-text">px</span>
          <span class="input-group-text bg-transparent border-0">×</span>
        </div>
        <input
          v-model.number="itemConfig.minHeight"
          type="number"
          step="1"
          min="0"
          class="form-control"
          placeholder="height"
          title="height"
          @mousedown.stop=""
        >
        <div class="input-group-append">
          <span class="input-group-text">px</span>
        </div>
      </div>
    </div>
    <div class="form-row">
      <div class="input-group">
        <div class="input-group-prepend">
          <span class="input-group-text bg-transparent border-0">Maximal</span>
        </div>
        <input
          v-model.number="itemConfig.maxWidth"
          type="number"
          step="1"
          min="0"
          class="form-control"
          placeholder="width"
          title="width"
          @mousedown.stop=""
        >
        <div class="input-group-append">
          <span class="input-group-text">px</span>
          <span class="input-group-text bg-transparent border-0">×</span>
        </div>
        <input
          v-model.number="itemConfig.maxHeight"
          type="number"
          step="1"
          min="0"
          class="form-control"
          placeholder="height"
          title="height"
          @mousedown.stop=""
        >
        <div class="input-group-append">
          <span class="input-group-text">px</span>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
export default {
    props: {
        item: { default: () => {}, type: Object }
    },
    data() {
        // copy config values from item
        const itemConfig = (({ resizeMethod, minWidth, maxWidth, minHeight, maxHeight }) => ({ resizeMethod, minWidth, maxWidth, minHeight, maxHeight }))(this.item);
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
                break;
            case 'auto':
                this.itemConfig.resizeMethod = 'auto';
                break;
            }
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


<style lang="postcss" scoped>
@import "../../style/variables.css";

.configDialog {
  min-width: 430px;
}
</style>
