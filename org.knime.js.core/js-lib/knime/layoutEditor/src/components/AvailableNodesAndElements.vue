<template>
  <div>
    <h4>Views <small>drag into layout or click</small></h4>
    <Draggable
      v-model="availableNodes"
      :options="{group: {name: 'content', pull: 'clone', put: false}, sort: false}"
      :clone="onCloneNode"
      :move="onMoveNode"
      element="ul"
      class="availableNodes"
    >
      <li
        v-for="(node, index) in availableNodes"
        :key="index"
        class="item"
        @click.prevent="onAvailableNodeClick(node)"
      >
        <div class="name">
          <img
            :src="node.icon"
          >
          <div
            :title="node.name"
          >
            {{ node.name }}
          </div>
          <small>Node {{ node.nodeID }}</small>
        </div>
        <div
          v-if="node.description && node.description.length"
          class="description"
        >
          {{ node.description }}
        </div>
      </li>
      <small v-if="availableNodes.length === 0">(all views are used in the layout)</small>
    </Draggable>

    <h4>Rows <small>drag into layout or click</small></h4>
    <Draggable
      v-model="$store.state.elements"
      :options="{group: {name: 'content', pull: 'clone', put: false}, sort: false}"
      :clone="onCloneElement"
      element="ul"
      class="availableElements"
    >
      <li
        v-for="(element, index) in $store.state.elements"
        :key="index"
        class="item"
        @click.prevent="onAvailableElementClick(element)"
      >
        {{ element.name }}
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
            const nodeIdsInLayout = this.$store.getters.getAllNodeIdsInLayout;
            return this.$store.state.nodes.filter(node => !nodeIdsInLayout.includes(node.nodeID));
        }
    },
    methods: {
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


<style lang="scss" scoped>
.availableNodes,
.availableElements {
  list-style: none;
  padding: 0;
  min-height: 30px;

  .item {
    cursor: move; // for IE11
    cursor: grab;
    border: 1px solid grey;
    padding: 2px 5px;
    margin-bottom: 2px;

    .name {
      display: flex;

      div {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        flex: 1;
        margin-right: 8px;
      }
      img {
        width: 16px;
        height: 16px;
        margin-right: 4px;
      }
      small {
        color: #777;
        white-space: nowrap;
      }
    }

    .description {
      margin-left: 20px;
    }
  }
}
</style>
