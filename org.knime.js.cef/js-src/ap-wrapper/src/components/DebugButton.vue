<script>
import InspectorIcon from '~/webapps-common/ui/assets/img/icons/code-html.svg?inline';

export default {
    components: {
        InspectorIcon
    },
    data() {
        return {
            debugPage: ''
        };
    },
    computed: {
        // Composed URL to either open the correct debugger or the overview page
        debugUrl() { return `http://localhost:8888${this.debugPage}`; }
    },
    mounted() {
        // Code taken from cef debugger
        let tabsListRequest = new XMLHttpRequest();
        tabsListRequest.open("GET", `http://localhost:8888/json/list?t=${new Date().getTime()}`, true);
        tabsListRequest.onreadystatechange = this.onReady;
        tabsListRequest.send();
    },
    methods: {
        // If more than one debugger is possible redirect to the debugger list, otherwise open the debugger directly
        onReady(e) {
            if (e.target.readyState === 4 && e.target.status === 200) {
                let responseJSON;
                if (e.target.response !== null) {
                    responseJSON = JSON.parse(e.target.response);
                }
                if (responseJSON.length > 1) {
                    this.debugPage = '';
                } else {
                    this.debugPage = responseJSON[0].devtoolsFrontendUrl;
                }
            }
        }
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

<style lang="postcss">

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
