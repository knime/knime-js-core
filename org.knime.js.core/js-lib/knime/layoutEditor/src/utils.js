export default {
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
