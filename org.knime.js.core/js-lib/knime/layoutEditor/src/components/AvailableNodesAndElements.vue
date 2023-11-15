<template>
  <div>
    <h4>Views <small class="text-muted">drag into layout or click</small></h4>
    <Draggable
      v-model="availableNodes"
      :options="{
        group: { name: 'content', pull: 'clone', put: false },
        sort: false,
        draggable: '.item',
      }"
      :clone="onCloneNode"
      :move="onMoveNode"
      element="ul"
      class="availableNodes"
      @start="$store.commit('setDragging', true)"
      @end="$store.commit('setDragging', false)"
    >
      <li
        v-for="(node, index) in availableNodes"
        :key="index"
        :class="['item', node.type]"
        @click.prevent="onAvailableNodeClick(node)"
      >
        <div class="name">
          <img :src="node.icon">
          <div :title="node.name">
            {{ node.name }}
          </div>
          <small class="text-muted">Node {{ node.nodeID }}</small>
        </div>
        <div
          v-if="node.description && node.description.length"
          class="description"
          :title="node.description"
        >
          {{ node.description }}
        </div>
      </li>
      <small
        v-if="availableNodes.length === 0"
        slot="footer"
      >
        (all views are used in the layout)
      </small>
    </Draggable>

    <h4>Rows <small class="text-muted">drag into layout or click</small></h4>
    <Draggable
      v-model="$store.state.elements"
      :options="{
        group: { name: 'content', pull: 'clone', put: false },
        sort: false,
      }"
      :clone="onCloneElement"
      element="ul"
      class="availableElements"
      @start="$store.commit('setDragging', true)"
      @end="$store.commit('setDragging', false)"
    >
      <li
        v-for="(element, index) in $store.state.elements"
        :key="index"
        :title="element.name"
        class="item row preview no-gutters align-items-center"
        @click.prevent="onAvailableElementClick(element)"
      >
        <div
          v-for="(column, colIndex) in element.data.columns"
          :key="colIndex"
          class="col"
        />
      </li>
    </Draggable>

    <h4>Print <small class="text-muted">drag into layout or click</small></h4>
    <Draggable
      v-model="$store.state.print"
      :options="{
        group: { name: 'content', pull: 'clone', put: false },
        sort: false,
      }"
      :clone="onCloneElement"
      element="ul"
      class="availableElements"
      @start="$store.commit('setDragging', true)"
      @end="$store.commit('setDragging', false)"
    >
      <li
        v-for="(element, index) in $store.state.print"
        :key="index"
        :title="element.name"
        class="item row preview no-gutters align-items-center print"
        @click.prevent="onAvailableElementClick(element)"
      >
        <div class="print-item">
          <span class="label">{{ element.name }}</span>
        </div>
      </li>
    </Draggable>
  </div>
</template>

<script>
import Draggable from 'vuedraggable';
import utils from '../utils';

export default {
    components: { Draggable },
    computed: {
        availableNodes() {
            return this.$store.getters.availableNodes;
        }
    },
    methods: {
        onDragStart(e) {
            return utils.dragStart(e);
        },
        onDragEnd(e) {
            return utils.dragEnd(e);
        },
        onMoveNode(e) {
            return utils.checkMove(e);
        },
        onCloneElement(element) {
            return JSON.parse(JSON.stringify(element.data));
        },
        onAvailableElementClick(element) {
            this.$store.commit('addElement', this.onCloneElement(element));
        },
        onCloneNode(node) {
            return utils.createViewFromNode(node);
        },
        onAvailableNodeClick(node) {
            this.$store.commit('addNode', node);
        }
    }
};
</script>

<style lang="postcss" scoped>
@import "../style/variables.css";

.availableNodes,
.availableElements {
  list-style: none;
  padding: 0;
  min-height: 30px;

  & .item {
    cursor: move; /* for IE11 */
    cursor: grab;
    border-radius: 3px;
    padding: 2px 5px;
    margin-bottom: 4px;

    &:hover,
    &:hover .print-item .label {
      background-color: var(--knime-view-preview-active);
    }
  }
}

.availableNodes {
  & .item {
    background-color: var(--knime-view-preview);

    &.quickform,
    &.configuration {
      background-color: var(--knime-configuration-preview);

      &:hover {
        background-color: var(--knime-configuration-preview-semi);
      }
    }

    & .name {
      display: flex;

      & div {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        flex: 1;
        margin-right: 8px;
      }
      & img {
        width: 16px;
        height: 16px;
        margin-right: 4px;
      }
      & small {
        white-space: nowrap;
      }
    }

    & .description {
      margin-left: 20px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

.availableElements {
  & .item {
    background-color: #ffffff;
  }

  & .row {
    border: 2px solid var(--knime-gray-ultra-light);
    height: 30px;
  }

  & .col {
    height: 70%;
  }

  & .col:not(:last-of-type) {
    border-right: 2px solid var(--knime-gray-ultra-light);
  }

  & .print-item {
    width: 100%;
    height: 11px;
    margin-bottom: 9px;
    text-align: center;
    color: var(--knime-silver-sand);
    border-bottom: 2px dotted var(--knime-gray-ultra-light);

    & .label {
      padding: 0 5px;
      background-color: #ffffff;
      line-height: 18px;
      vertical-align: middle;
    }
  }
}
</style>
