import Vue from 'vue';

export default {
    setColumnWidths(column, width) {
        // actually we don't support responsive layouts yet; so we only set XS and remove all others
        // using Vue.set() to get reactivity working (see https://vuejs.org/v2/guide/reactivity.html#Change-Detection-Caveats)
        Vue.set(column, 'widthXS', width);

        Vue.delete(column, 'widthSM');
        Vue.delete(column, 'widthMD');
        Vue.delete(column, 'widthLG');
        Vue.delete(column, 'widthXL');
        return column;
    },
    createViewFromNode(node) {
        return JSON.parse(JSON.stringify(node.layout));
    },
    checkMove(e) {
        // only allow rows to be dropped in first level
        const targetComponent = e.relatedContext.component;
        if (targetComponent && targetComponent.$attrs && targetComponent.$attrs['is-first-level']) {
            if (e.draggedContext.element.type === 'row') {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
};
