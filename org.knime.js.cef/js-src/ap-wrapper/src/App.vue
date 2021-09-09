<script>
import UIExtComponent from './components/UIExtComponent.vue';
import UIExtIFrame from './components/UIExtIFrame.vue';

export default {
    components: {
        UIExtComponent,
        UIExtIFrame
    },
    data() {
        return {
            info: {}
        };
    },
    mounted() {
        // TODO NXT-653 use knime service to provide the information
        this.info = JSON.parse(window.getNodeViewInfo());
    }
};
</script>

<template>
  <div v-if="info.uicomponent">
    <UIExtComponent
      :name="info.name"
      :component-src="info.url"
      :project-id="info.projectId"
      :workflow-id="info.workflowId"
      :node-id="info.nodeId"
      :init-data="info.initData"
    />
  </div>
  <div v-else>
    <UIExtIFrame
      :iframe-src="info.url"
      :project-id="info.projectId"
      :workflow-id="info.workflowId"
      :node-id="info.nodeId"
      :init-data="info.initData"
    />
  </div>
</template>

<style lang="postcss">
</style>
