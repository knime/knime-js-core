import Vue from 'vue';

import App from './components/App';
import store from './store';

Vue.config.productionTip = false;

new Vue({
    el: document.body.appendChild(document.createElement('div')),
    store,
    created() {
        window.loadNodes = nodes => {
            console.log(`loadNodes ${nodes}`);
            this.$store.commit('loadNodes', JSON.parse(nodes));
        };
        window.loadLayout = layout => {
            console.log(`loadLayout ${layout}`);
            try {
                this.$store.commit('loadLayout', JSON.parse(layout));
            } catch (e) {
                // e.g. wasn't valid JSON
            }
        };
        window.sendLayout = () => {
            console.log(`sendLayout`);
            return JSON.stringify(this.$store.state.layout);
        };
    },
    render: render => render(App)
});

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
