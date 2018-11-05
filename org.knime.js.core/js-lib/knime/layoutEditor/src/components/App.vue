<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-3 controls">
        <label>
          <input
            v-model="debugMode"
            type="checkbox"
          > debug mode
        </label>

        <br>
        <button
          title="remove all views and rows"
          @click="onClear"
        >
          clear layout
        </button>&nbsp;
        <button
          title="revert to initial state"
          @click="onReset"
        >
          reset layout
        </button>
        <br><br>

        <AvailableNodesAndElements />
      </div>

      <div :class="[debugMode ? 'col-6' : 'col-9', 'layout']">
        <Draggable
          v-model="rows"
          :options="{group: 'content', isFirstLevel: true}"
          class="container-fluid layoutPreview"
        >
          <Row
            v-for="(row, index) in rows"
            :key="index"
            :row="row"
            :deletable="rows.length > 1"
          />
        </Draggable>
      </div>
      <AdvancedEditor
        v-if="debugMode"
        class="col-3 debug"
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
        onReset(e) {
            this.$store.commit('resetLayout');
        },
        onSplitVertical() {
            this.$store.commit('splitVertical');
        }
    }
};
</script>


<style lang="postcss">
@import "../style/variables.css";

body {
  overflow: hidden; /* prevent scrolling */
}

* {
  box-sizing: border-box;
}

.controls,
.debug {
  background-color: #f5f5f5;
  height: 100vh;
}

.controls {
  overflow: auto;
}

.layout {
  overflow-y: scroll;
  height: 100vh;
  padding-top: 20px;
  min-height: 100px;
}

.layoutPreview {
  min-height: 100%; /* fill height to be a drag zone on first level */
  padding-bottom: 20px;
}
</style>
