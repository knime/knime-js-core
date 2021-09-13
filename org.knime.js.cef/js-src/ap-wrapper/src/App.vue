<script>
import UIExtComponent from './components/UIExtComponent.vue';
import UIExtIFrame from './components/UIExtIFrame.vue';
import DebugButton from './components/DebugButton.vue';
import RefreshButton from './components/RefreshButton.vue';

export default {
    components: {
        UIExtComponent,
        UIExtIFrame,
        RefreshButton,
        DebugButton
    },
    data() {
        return {
            info: {}
        };
    },
    computed: {
        // eslint-disable-next-line no-process-env
        isDebug: () => process.env.NODE_ENV === 'development'
    },
    mounted() {
        // TODO NXT-653 use knime service to provide the information
        this.info = JSON.parse(window.getNodeViewInfo());
    }
};
</script>

<template>
  <div>
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
    <DebugButton v-if="isDebug" />
    <RefreshButton v-if="info.uicomponent" />
  </div>
</template>

<style lang="postcss">

</style>
