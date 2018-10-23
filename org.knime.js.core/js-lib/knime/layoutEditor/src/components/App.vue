<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-xs-2 controls">
        <label>
          <input
            v-model="debugMode"
            type="checkbox"
          > debug mode
        </label>
        <br>
        <button @click="onClear">clear layout</button>&nbsp;

        <template v-if="$store.state.selectionMode">
          <br>
          <button
            :disabled="!($store.state.selectedItem && $store.state.selectedItem.type === 'row')"
            @click="onSplitVertical"
          >
            split |
          </button>
        </template>

        <br><br>

        <AvailableNodesAndElements />
      </div>

      <div :class="[debugMode ? 'col-xs-7' : 'col-xs-10', 'layout']">
        <Draggable
          v-model="rows"
          :options="{group: 'content', isFirstLevel: true}"
          :class="[{ editMode: $store.state.editMode}, 'container-fluid']"
        >
          <Row
            v-for="(row, index) in rows"
            :key="index"
            :row="row"
          />
        </Draggable>
      </div>
      <AdvancedEditor
        v-if="debugMode"
        class="col-xs-3"
      />
    </div>
  </div>
</template>


<script>
import Draggable from 'vuedraggable';
import Row from './layout/Row';
import AvailableNodesAndElements from './AvailableNodesAndElements';
import AdvancedEditor from './AdvancedEditor';

export default {
    components: { Draggable, Row, AvailableNodesAndElements, AdvancedEditor },
    data() {
        return {
            debugMode: false
        };
    },
    computed: {
        rows: {
            get() {
                return this.$store.state.layout.rows;
            },
            set(value) {
                this.$store.commit('updateFirstLevelRows', value);
            }
        }
    },
    methods: {
        onClear(e) {
            this.$store.commit('clearLayout');
        },
        onSplitVertical() {
            this.$store.commit('splitVertical');
        }
    }
};
</script>


<style lang="scss">
.controls {
  background-color: rgb(240, 240, 240);
  height: 100vh;
  overflow: auto;

  label {
    margin-right: 10px;
  }
}
.layout {
  overflow: scroll;
  height: 100vh;
  padding-top: 20px;
  min-height: 100px;
}
.debug {
  background-color: rgb(240, 240, 240);
  height: 100vh;
}

.editMode {
  padding: 30px; // needed for drag & drop

  .row {
    border: 4px solid pink;
    min-height: 30px;
    position: relative; // only for right delete handle position

    .deleteHandle {
      width: 10px;
      height: 10px;
      background-color: red;
      position: absolute;
      right: 0;
      top: 0;
      cursor: pointer;
      color: #fff;
      line-height: 7px;
      text-align: center;
    }
  }
}
</style>
