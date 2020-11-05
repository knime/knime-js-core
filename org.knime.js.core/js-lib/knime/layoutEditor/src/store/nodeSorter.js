const getEmptyLayout = function () {
    return {
        rows: [{
            type: 'row',
            columns: [{
                content: []
            }]
        }]
    };
};


// warn on state mutations outside mutation handlers when in dev mode
export const strict = Boolean(window.webpackHotUpdate);

export const state = () => ({
    layout: getEmptyLayout(),
    nodes: []
});

export const mutations = {
    setLayout(state, layout) {
        const layoutAsString = JSON.stringify(layout);

        // replace current layout with new one
        state.layout = JSON.parse(layoutAsString);
    },

    setNodes(state, nodes) {
        state.nodes = nodes;
    },

    // called by vuedraggable on reorder
    updateColumnContent(state, newContent) {
        state.layout.rows[0].columns[0].content = newContent;
    }
};

export const getters = {
    getColumnContent(state) {
        return state.layout.rows[0].columns[0].content;
    }
};
