import Vue from 'vue';

export default {
    setColumnWidths(column, width) {
        // actually we don't support responsive layouts yet; so we set all to the same width
        // using Vue.set() to get reactivity working (see https://vuejs.org/v2/guide/reactivity.html#Change-Detection-Caveats)
        Vue.set(column, 'widthXS', width);
        Vue.set(column, 'widthSM', width);
        Vue.set(column, 'widthMD', width);
        Vue.set(column, 'widthLG', width);
        Vue.set(column, 'widthXL', width); // TODO remove because not available in Bootstrap 3?
        return column;
    },
    createViewFromNode(node) {
        const view = Object.assign({}, node.layout);
        view.type = 'view';
        view.nodeID = node.nodeID;
        return view;
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
