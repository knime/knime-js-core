<script>
import Vue from 'vue';

export default {
    components: {
        // UIExtension
    },
    props: {
        name: {
            type: String,
            required: true
        },
        projectId: {
            type: String,
            required: true
        },
        nodeId: {
            type: String,
            required: true
        },
        workflowId: {
            type: String,
            required: true
        },
        componentSrc: {
            type: String,
            required: true
        },
        initData: {
            type: String,
            required: false,
            default: ''
        }
    },
    data() {
        return {
            componentLoaded: false
        };
    },
    async mounted() {
        if (!this.componentLoaded) {
            await this.loadComponentLibrary();
        }
    },
    methods: {
        async loadComponentLibrary() {
            // set dehydrated component lib dependencies globally
            window.Vue = Vue;
            // Load and mount component library
            await new Promise((resolve, reject) => {
                const script = document.createElement('script');
                let url = this.componentSrc;
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
            let Component = window[this.name];
            if (!Component) {
                throw new Error(`Component loading failed. Script invalid.`);
            }
            delete window[this.name];
            this.$options.components[this.name] = Component;
            this.componentLoaded = true;
        }
    }
};
</script>

<template>
  <component
    :is="name"
    v-if="componentLoaded"
    :init-data="initData"
  />
</template>

<style lang="postcss">
@import 'modern-normalize/modern-normalize.css';
@import "webapps-common/ui/css/variables";
@import "webapps-common/ui/css/basics";
@import "webapps-common/ui/css/fonts";
</style>
