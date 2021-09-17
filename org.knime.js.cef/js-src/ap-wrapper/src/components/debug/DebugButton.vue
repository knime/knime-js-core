<script>
import FunctionButton from '~/webapps-common/ui/components/FunctionButton';
import InspectorIcon from '~/webapps-common/ui/assets/img/icons/code-html.svg?inline';

export default {
    components: {
        FunctionButton,
        InspectorIcon
    },
    props: {
        debugPort: {
            type: String,
            required: true
        }
    },
    data() {
        return {
            debugPage: ''
        };
    },
    computed: {
        // Composed URL to either open the correct debugger or the overview page
        debugUrl() {
            return `http://localhost:${this.debugPort}${this.debugPage}`;
        }
    },
    async mounted() {
        // get all existing debugger instances
        let debuggerInstances = await fetch(`http://localhost:${this.debugPort}/json/list?t=${new Date().getTime()}`)
            .then(response => response.json());

        // when there is only 1 instance we open it (otherwise the list of all instances is displayed)
        if (debuggerInstances.length === 1) {
            this.debugPage = debuggerInstances[0].devtoolsFrontendUrl;
        }
    }
};
</script>

<template>
  <FunctionButton
    primary
    :href="debugUrl"
    target="_blank"
    class="button"
    title="Open developer tools"
  >
    <InspectorIcon />
  </FunctionButton>
</template>

<style lang="postcss" scoped>
.button {
  position: fixed;
  bottom: 10px;
  right: 10px;
}
</style>
