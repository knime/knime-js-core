<script>
import InspectorIcon from '~/webapps-common/ui/assets/img/icons/code-html.svg?inline';

export default {
    components: {
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
        debugUrl() { return `http://localhost:${this.debugPort}${this.debugPage}`; }
    },
    async mounted() {
        // Checks for existing debugger instances.
        // If there is more than 1, the list of all instances is displayed.
        // Otherwise, the first one will be opened.
        this.debugPage = await fetch(`http://localhost:${this.debugPort}/json/list?t=${new Date().getTime()}`)
            .then(response => response.json())
            .then(data => data.length === 1 ? data[0].devtoolsFrontendUrl : '');
    }
};
</script>

<template>
  <div class="container">
    <a
      :href="debugUrl"
      target="_blank"
      class="float"
    >
      <p class="icon">
        <InspectorIcon />
      </p>
    </a>
  </div>
</template>

<style lang="postcss" scoped>

.container {
  position: fixed;
  width: 40px;
  height: 40px;
  bottom: 10px;
  right: 10px;
  display: flex;
  background-color: rgb(238, 250, 255);
  color: rgb(0, 0, 0);
  border-radius: 50px;
  box-shadow: 1px 1px 3px rgb(153, 153, 153);
  justify-content: center;
  align-items: center;
}

.icon {
  width: 25px;
  height: 25px;
}
</style>
