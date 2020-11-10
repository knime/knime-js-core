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
        ...mapGetters(['getRows', 'getNodeDetails']),
        rows: {
            get() {
                return this.getRows;
            },
            set(value) {
                this.$store.commit('updateRows', value);
            }
        }
    }
};
</script>

<template>
  <Draggable
    v-model="rows"
    class="container-fluid layout"
  >
    <KnimeView
      v-for="(row, index) in rows"
      :key="index"
      :view="row.columns[0].content[0]"
      class="item"
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

  & .item {
    cursor: move;
    margin: 5px 0;
    min-height: 60px !important;
    max-height: 60px;
  }
}
</style>
