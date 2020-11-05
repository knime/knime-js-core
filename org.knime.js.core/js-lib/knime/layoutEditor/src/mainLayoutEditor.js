import Vue from 'vue';
import Vuex from 'vuex';

import App from './components/App';
import * as storeConfig from './store/layoutEditor';

Vue.config.productionTip = false;
Vue.use(Vuex);


// detect KNIME AP debug mode and make it available in all components
Vue.prototype.$debug = Boolean(window.Firebug);

const app = new Vue({
    store: new Vuex.Store(storeConfig),
    created() {
        // global methods to be called by AP
        window.setNodes = nodes => {
            if (this.$debug) {
                console.log('setNodes', nodes); // eslint-disable-line no-console
            }
            this.$store.commit('setNodes', JSON.parse(nodes));
        };
        window.setLayout = layout => {
            try {
                let parsedLayout = JSON.parse(layout);
                // if we don't have a legacy mode set for whatever reason, it must be legacy
                if (typeof parsedLayout.parentLayoutLegacyMode === 'undefined') {
                    parsedLayout.parentLayoutLegacyMode = true;
                }
                if (this.$debug) {
                    console.log('setLayout', parsedLayout); // eslint-disable-line no-console
                }
                this.$store.commit('setLayout', parsedLayout);
            } catch (e) {
                // e.g. wasn't valid JSON
            }
        };

        // push layout changes to AP
        this.$store.watch(state => state.layout, (newLayout, oldLayout) => {
            if (typeof window.pushLayout === 'function') {
                const layout = JSON.stringify(newLayout);
                if (this.$debug) {
                    console.log('pushLayout', layout); // eslint-disable-line no-console
                }
                window.pushLayout(layout);
            }
        }, { deep: true });
    },
    render: render => render(App)
});

// wait until DOM is ready because AP will load this app in the HTML head
document.addEventListener('DOMContentLoaded', () => {
    app.$mount(document.body.appendChild(document.createElement('div')));
});
