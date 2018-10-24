import Vue from 'vue';

import App from './components/App';
import store from './store';

Vue.config.productionTip = false;

const app = new Vue({
    store,
    created() {
        // global methods to be called by AP
        window.setNodes = nodes => {
            console.log(`setNodes ${nodes}`);
            this.$store.commit('setNodes', JSON.parse(nodes));
        };
        window.setLayout = layout => {
            console.log(`setLayout ${layout}`);
            try {
                this.$store.commit('setLayout', JSON.parse(layout));
            } catch (e) {
                // e.g. wasn't valid JSON
            }
        };

        // push layout changes to AP
        this.$store.watch(state => state.layout, (newLayout, oldLayout) => {
            console.warn(`pushLayout ${JSON.stringify(newLayout)}`);
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

    // only for debugging
    console.log = function (message) {
        let el = document.getElementById('console');
        if (!el) {
            el = document.body.appendChild(document.createElement('pre'));
            el.setAttribute('id', 'console');
            el.setAttribute('style', 'font-size: 10px; overflow: scroll; white-space: pre;');
        }
        el.innerHTML += `${message}\n\n`;
    };

});
