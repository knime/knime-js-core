import Vue from 'vue';

export default {
    setColumnWidths(column, width) {
        // actually we don't support responsive layouts yet; so we set all to the same width
        // using Vue.set() to get reactivity working (see https://vuejs.org/v2/guide/reactivity.html#Change-Detection-Caveats)
        Vue.set(column, 'widthXS', width);
        Vue.set(column, 'widthSM', width);
        Vue.set(column, 'widthMD', width);
        Vue.set(column, 'widthLG', width);
        Vue.set(column, 'widthXL', width);
        return column;
    },
    createViewFromNode(node) {
        return JSON.parse(JSON.stringify(node.layout));
    },
    checkMove(e) {
        // only allow rows to be dropped in first level
        const targetComponent = e.relatedContext.component;
        if (targetComponent && targetComponent.options && targetComponent.options.isFirstLevel) {
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
