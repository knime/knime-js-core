<script>
export default {
    props: {
        projectId: {
            type: String,
            required: true
        },
        workflowId: {
            type: String,
            required: true
        },
        nodeId: {
            type: String,
            required: true
        },
        iframeSrc: {
            type: String,
            required: true
        },
        initData: {
            type: String,
            required: false,
            default: ''
        }

    },
    mounted() {
        window.addEventListener('message', this.onMessageFromIFrame);
    },
    beforeDestroy() {
        window.removeEventListener('message', this.onMessageFromIFrame);
    },
    methods: {
        onMessageFromIFrame(message) {
            let { contentWindow } = this.$refs.iframe;
            if (message.source !== contentWindow) {
                return;
            }

            if (message.data.type === 'knime-ready') {
                contentWindow.postMessage({
                    type: 'knime-init',
                    data: {
                        projectId: this.projectId,
                        workflowId: this.workflowId,
                        nodeId: this.nodeId,
                        initData: this.initData
                    }
                }, '*');
            }
        }
    }
};
</script>

<template>
  <div>
    <iframe
      ref="iframe"
      :src="iframeSrc"
    />
  </div>
</template>

<style lang="postcss" scoped>
iframe {
  width: 100%;
  height: 100%;
}
</style>
