<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-3 controls">
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

        <div v-if="$debug">
          <br>
          <label>
            <input
              v-model="showDebugInfo"
              type="checkbox"
            > show JSON
          </label>
        </div>
      </div>

      <div class="col layout">
        <div
          v-if="isResponsiveLayout"
          class="alert alert-warning"
          role="alert"
        >
          The visual editor doesn’t support responsive layouts yet.
          Please use advanced editor or responsive settings will get lost.
        </div>
        <div
          v-if="isWrappingLayout"
          class="alert alert-warning"
          role="alert"
        >
          Your layout has rows with a total column width larger than {{ gridSize }}, therefore the columns will wrap.
          The visual editor doesn’t support wrapping layouts yet. Please use advanced editor instead.
        </div>

        <Draggable
          v-model="rows"
          :options="{group: 'content', isFirstLevel: true}"
          :class="['container-fluid', 'layoutPreview', {'droppable': $store.state.dragging === 'row'}]"
          @start="$store.commit('setDragging', true)"
          @end="$store.commit('setDragging', false)"
        >
          <Row
            v-for="(row, index) in rows"
            :key="index"
            :row="row"
            :deletable="rows.length > 1"
          />
        </Draggable>

        <p
          v-if="availableNodes.length"
          class="hint text-muted text-center"
        >
          <small>Views not added into the layout and not disabled in 'Node Usage' will be shown below layout.</small>
        </p>
      </div>

      <Debug
        v-if="showDebugInfo"
        class="col-3 debug"
      />
    </div>
  </div>
</template>


<script>
import Draggable from 'vuedraggable';
import Row from './layout/Row';
import AvailableNodesAndElements from './AvailableNodesAndElements';
import Debug from './Debug';
import config from '../config';

export default {
    components: { Draggable, Row, AvailableNodesAndElements, Debug },
    data() {
        return {
            showDebugInfo: false
        };
    },
    computed: {
        isResponsiveLayout() {
            return this.$store.getters.isResponsiveLayout;
        },
        isWrappingLayout() {
            return this.$store.getters.isWrappingLayout;
        },
        gridSize() {
            return config.gridSize;
        },
        availableNodes() {
            return this.$store.getters.availableNodes;
        },
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

*:not(input, textarea) {
  user-select: none; /* disable selection everywhere */
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

  & > .hint {
    line-height: 100%;
  }
}

.layoutPreview {
  /* fill height to be a drag zone on first level */
  min-height: calc(100% - 40px);
  padding-bottom: 20px;

  outline-width: 2px;
  outline-style: dashed;
  outline-offset: 2px;
  outline-color: transparent;
  transition: outline-color 0.3s;

  &.droppable {
    outline-color: var(--knime-yellow-sec-server);
  }
}
</style>
