<script>
import Vue from 'vue';
import { KnimeService } from 'knime-ui-extension-service';

export default {
    components: {
        // UIExtension
    },
    props: {
        extInfo: {
            type: Object,
            required: false,
            default: null
        }
    },
    data() {
        return {
            componentLoaded: false,
            knimeService: null
        };
    },
    async mounted() {
        this.knimeService = new KnimeService(this.extInfo);
        if (!this.componentLoaded) {
            await this.loadComponentLibrary();
        }
    },
    methods: {
        async loadComponentLibrary() {
            let { url, name } = this.extInfo;
                        
            // set dehydrated component lib dependencies globally
            window.Vue = Vue;
            // Load and mount component library
            await new Promise((resolve, reject) => {
                const script = document.createElement('script');
                script.async = true;
                script.addEventListener('load', () => {
                    resolve(script);
                });
                script.addEventListener('error', () => {
                    reject(new Error(`Script loading of "${url}" failed`));
                    document.head.removeChild(script);
                });
                script.src = url;
                document.head.appendChild(script);
            });

            // lib build mounts component globally under package (.json) name
            let Component = window[name];
            if (!Component) {
                throw new Error(`Component loading failed. Script invalid.`);
            }
            delete window[name];
            this.$options.components[name] = Component;
            this.componentLoaded = true;
        }
    }
};
</script>

<template>
  <component
    :is="extInfo.name"
    v-if="componentLoaded"
    :knime-service="knimeService"
  />
</template>
