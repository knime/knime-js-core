import Vue from 'vue';

import App from './components/App';
import store from './store';

Vue.config.productionTip = false;

const app = new Vue({
    store,
    created() {
        // global methods to be called by AP
        window.setNodes = nodes => {
            this.$store.commit('setNodes', JSON.parse(nodes));
        };
        window.setLayout = layout => {
            try {
                this.$store.commit('setLayout', JSON.parse(layout));
            } catch (e) {
                // e.g. wasn't valid JSON
            }
        };

        // push layout changes to AP
        this.$store.watch(state => state.layout, (newLayout, oldLayout) => {
            if (typeof window.pushLayout === 'function') {
                window.pushLayout(JSON.stringify(newLayout));
            }
        }, { deep: true });
    },
    render: render => render(App)
});

// wait until DOM is ready because AP will load this app in the HTML head
document.addEventListener('DOMContentLoaded', () => {
    app.$mount(document.body.appendChild(document.createElement('div')));
});
