import Vue from 'vue';
import Vuex from 'vuex';
import config from '../config';
import utils from '../utils';

Vue.use(Vuex);

const getEmptyLayout = function () {
    return {
        rows: [
            {
                type: 'row',
                columns: [{
                    content: [],
                    widthMD: config.gridSize,
                    widthLG: config.gridSize,
                    widthXS: config.gridSize,
                    widthXL: config.gridSize,
                    widthSM: config.gridSize
                }]
            }
        ]
    };
};

const getAllColumnArrays = function (layout) {
    return layout.reduce((result, item) => {
        if (item.type === 'row' && Array.isArray(item.columns) && item.columns.length) {
            result.push(item.columns);
            item.columns.forEach((column) => {
                if (Array.isArray(column.content) && column.content.length) {
                    result = result.concat(getAllColumnArrays(column.content));
                }
            });
        }
        return result;
    }, []);
};

const getAllContentArrays = function (layout) {
    const allColumnArrays = getAllColumnArrays(layout);
    const allContentArrays = allColumnArrays.reduce((result, columns) => {
        columns.forEach(column => {
            if (Array.isArray(column.content) && column.content.length) {
                result.push(column.content);
            }
        });
        return result;
    }, []);

    // add first level as well
    allContentArrays.push(layout);

    return allContentArrays;
};


export default new Vuex.Store({
    strict: Boolean(window.webpackHotUpdate), // warn on state mutations outside mutation handlers when in dev mode
    state: {
        editMode: true,
        selectionMode: false,
        selectedItem: null,
        livePreview: false,
        resizeColumnInfo: null,
        layout: getEmptyLayout(),
        nodes: [],
        elements: [
            {
                name: 'Row 1-column',
                data: {
                    type: 'row',
                    columns: [{ content: [], widthMD: 12, widthLG: 12, widthXS: 12, widthXL: 12, widthSM: 12 }]
                }
            },
            {
                name: 'Row 2-columns',
                data: {
                    type: 'row',
                    columns: [
                        { content: [], widthMD: 6, widthLG: 6, widthXS: 6, widthXL: 6, widthSM: 6 },
                        { content: [], widthMD: 6, widthLG: 6, widthXS: 6, widthXL: 6, widthSM: 6 }
                    ]
                }
            },
            {
                name: 'Row 3-columns',
                data: {
                    type: 'row',
                    columns: [
                        { content: [], widthMD: 4, widthLG: 4, widthXS: 4, widthXL: 4, widthSM: 4 },
                        { content: [], widthMD: 4, widthLG: 4, widthXS: 4, widthXL: 4, widthSM: 4 },
                        { content: [], widthMD: 4, widthLG: 4, widthXS: 4, widthXL: 4, widthSM: 4 }
                    ]
                }
            },
            {
                name: 'Row 4-columns',
                data: {
                    type: 'row',
                    columns: [
                        { content: [], widthMD: 3, widthLG: 3, widthXS: 3, widthXL: 3, widthSM: 3 },
                        { content: [], widthMD: 3, widthLG: 3, widthXS: 3, widthXL: 3, widthSM: 3 },
                        { content: [], widthMD: 3, widthLG: 3, widthXS: 3, widthXL: 3, widthSM: 3 },
                        { content: [], widthMD: 3, widthLG: 3, widthXS: 3, widthXL: 3, widthSM: 3 }
                    ]
                }
            }
        ]
    },
    getters: {
        getAllNodeIdsInLayout(state) {
            const allContentArrays = getAllContentArrays(state.layout.rows);
            return allContentArrays.flat().filter(item => item.type === 'view').map(item => item.nodeID);
        }
    },
    mutations: {
        loadLayout(state, layout) {
            // replace current layout with new one
            state.layout = JSON.parse(JSON.stringify(layout));
        },
        loadNodes(state, nodes) {
            state.nodes = JSON.parse(JSON.stringify(nodes));
        },
        initialLayout(state) {
            const content = [];

            state.nodes.forEach(node => {
                content.push(utils.createViewFromNode(node));
            });

            const rows = [{
                type: 'row',
                columns: [{
                    content,
                    widthMD: 12,
                    widthLG: 12,
                    widthXS: 12,
                    widthXL: 12,
                    widthSM: 12
                }]
            }];

            state.layout = {
                rows
            };
        },
        clearLayout(state) {
            // remove all rows and columns and add one row
            state.layout = getEmptyLayout();
        },

        setResizeColumnInfo(state, resizeColumnInfo) {
            state.resizeColumnInfo = resizeColumnInfo;
        },

        resizeColumn(state, info) {
            const oldWidth = info.column.widthMD;
            const delta = oldWidth - info.newWidth;

            const allColumnArrays = getAllColumnArrays(state.layout.rows);
            for (let columnArray of allColumnArrays) {
                let index = columnArray.indexOf(info.column);
                if (index >= 0) {
                    // resize siblings
                    const sibling = columnArray[index + 1];
                    if (sibling) {
                        sibling.widthMD += delta;
                    }

                    break;
                }
            }

            // currently we don't support responsive layouts, so set all sizes
            info.column.widthMD = info.newWidth;
            info.column.widthLG = info.newWidth;
            info.column.widthXS = info.newWidth;
            info.column.widthXL = info.newWidth;
            info.column.widthSM = info.newWidth;
        },

        deleteColumn(state, columnToDelete) {
            const allColumnArrays = getAllColumnArrays(state.layout.rows);

            for (let columnArray of allColumnArrays) {
                let index = columnArray.indexOf(columnToDelete);
                if (index >= 0) {
                    // resize siblings to fill up space
                    const lostWidth = columnToDelete.widthMD;
                    const sibling1 = columnArray[index - 1];
                    const sibling2 = columnArray[index + 1];
                    if (sibling1 && sibling2) {
                        const numberOfColumnsToSplit = 2;
                        if (lostWidth % numberOfColumnsToSplit === 0) {
                            const delta = lostWidth / numberOfColumnsToSplit;
                            sibling1.widthMD += delta;
                            sibling2.widthMD += delta;
                        } else {
                            sibling1.widthMD += lostWidth;
                        }
                    } else if (sibling1) {
                        sibling1.widthMD += lostWidth;
                    } else {
                        sibling2.widthMD += lostWidth;
                    }

                    // remove column
                    columnArray.splice(index, 1);

                    break;
                }
            }
        },

        deleteContentItem(state, itemToDelete) {
            const allContentArrays = getAllContentArrays(state.layout.rows);

            for (let contentArray of allContentArrays) {
                let index = contentArray.indexOf(itemToDelete);
                if (index >= 0) {
                    // remove item
                    contentArray.splice(index, 1);
                    break;
                }
            }
        },

        updateColumns(state, data) {
            data.row.columns = data.newColumns;
        },

        updateContent(state, data) {
            data.column.content = data.newContent;
        },

        updateFirstLevelRows(state, rows) {
            state.layout.rows = rows;
        },

        setSelection(state, item) {
            state.selectedItem = item;
        },
        splitVertical(state) {
            if (state.selectedItem && state.selectedItem.type === 'row') {
                if (state.selectedItem.columns.length === config.gridSize) {
                    return;
                }

                const width = Math.round(config.gridSize / (state.selectedItem.columns.length + 1));
                state.selectedItem.columns.push({
                    content: []
                });

                const sizes = {
                    widthMD: width,
                    widthLG: width,
                    widthXS: width,
                    widthXL: width,
                    widthSM: width
                };

                state.selectedItem.columns.forEach(column => {
                    Object.assign(column, sizes);
                });
            }
        },

        addColumn(state, row) {
            let newNumberOfColumns = row.columns.length + 1;
            const width = Math.floor(config.gridSize / newNumberOfColumns);
            row.columns.push({
                content: []
            });

            const sizes = {
                widthMD: width,
                widthLG: width,
                widthXS: width,
                widthXL: width,
                widthSM: width
            };
            row.columns.forEach(column => {
                Object.assign(column, sizes);
            });

            const totalWidth = width * newNumberOfColumns;
            if (totalWidth < config.gridSize) {
                const delta = config.gridSize - totalWidth;
                row.columns[row.columns.length - 1].widthMD += delta;
            }

            row.columns = JSON.parse(JSON.stringify(row.columns)); // why needed?
        },

        addNode(state, node) {
            // add node to last column in last row
            const row = state.layout.rows[state.layout.rows.length - 1];
            row.columns[row.columns.length - 1].content.push(utils.createViewFromNode(node));
        }
    }
});
