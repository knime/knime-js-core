<script>
import UIExtComponent from './components/UIExtComponent.vue';
import UIExtIFrame from './components/UIExtIFrame.vue';
import DebugButton from './components/debug/DebugButton.vue';
import RefreshButton from './components/debug/RefreshButton.vue';

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
    <template v-if="info.remoteDebugPort">
      <DebugButton :debug-port="info.remoteDebugPort" />
      <RefreshButton v-if="info.uicomponent" />
    </template>
  </div>
</template>

<style lang="postcss">
@import 'modern-normalize/modern-normalize.css';
@import "webapps-common/ui/css/variables";
@import "webapps-common/ui/css/basics";
@import "webapps-common/ui/css/fonts";
</style>
