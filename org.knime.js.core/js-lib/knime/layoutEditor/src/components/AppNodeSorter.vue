<script>
import { mapGetters } from 'vuex';
import Draggable from 'vuedraggable';
import KnimeView from './layout/KnimeView';

export default {
    components: {
        Draggable,
        KnimeView
    },
    computed: {
        ...mapGetters(['getColumnContent', 'getNodeDetails']),
        content: {
            get() {
                return this.getColumnContent;
            },
            set(value) {
                this.$store.commit('updateColumnContent', value);
            }
        }
    }
};
</script>

<template>
  <Draggable
    v-model="content"
    class="container-fluid layout"
  >
    <KnimeView
      v-for="(view, index) in content"
      :key="index"
      :view="view"
      class="view"
    />
  </Draggable>
</template>

<style lang="postcss">
@import "../style/variables.css";

body {
  overflow-y: scroll;
}

* {
  box-sizing: border-box;
}

html,
body {
  font-size: 12px;
}

*:not(input, textarea) {
  user-select: none; /* disable selection everywhere */
}

.layout {
  /* fill height to be a drag zone on first level */
  min-height: calc(100vh - 50px);

  & .sortable-ghost {
    background-color: transparent;
    height: 60px;

    & * {
      display: none; /* for now we just hide the content, maybe there is a better way to render the ghost */
    }
  }
}

.view {
  cursor: move;
  margin: 5px 0;
  min-height: 60px !important;
  max-height: 60px;
}
</style>
