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
        this.info.initData = JSON.parse(this.info.initData);
    }
};
</script>

<template>
  <div v-if="info.uicomponent">
    <UIExtComponent
      :name="info.name"
      :componentSrc="info.url" 
      :projectId ="info.projectId" 
      :workflowId="info.workflowId" 
      :nodeId="info.nodeId"
      :initData ="info.initData"
    />
  </div>
  <div v-else>
    <UIExtIFrame
      :iframeSrc="info.url" 
      :projectId ="info.projectId" 
      :workflowId="info.workflowId" 
      :nodeId="info.nodeId"
      :initData ="info.initData"
    />
  </div>
</template>

<style lang="postcss">
</style>
