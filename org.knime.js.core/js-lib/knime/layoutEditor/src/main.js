import Vue from 'vue';

import App from './components/App';
import store from './store';

Vue.config.productionTip = false;

// detect KNIME AP debug mode and make it available in all components
Vue.prototype.$debug = Boolean(window.Firebug);

const app = new Vue({
    store,
    created() {
        // global methods to be called by AP
        window.setNodes = nodes => {
            if (this.$debug) {
                console.log('setNodes', nodes); // eslint-disable-line no-console
            }
            this.$store.commit('setNodes', JSON.parse(nodes));
        };
        window.setLayout = layout => {
            if (this.$debug) {
                console.log('setLayout', layout); // eslint-disable-line no-console
            }
            try {
                this.$store.commit('setLayout', JSON.parse(layout));
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
