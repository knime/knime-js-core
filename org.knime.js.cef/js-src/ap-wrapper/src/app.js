import Vue from 'vue';
import App from './App.vue';
import consola from 'consola';

Vue.config.productionTip = false;

// global KnimeService
// eslint-disable-next-line new-cap
window.consola = consola;

new Vue({
    render: h => h(App)
}).$mount('#app');
