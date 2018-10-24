import Vue from 'vue';
import Vuex from 'vuex';
import config from '../config';
import utils from '../utils';

Vue.use(Vuex);

const getEmptyLayout = function () {
    const column = {
        content: []
    };
    utils.setColumnWidths(column, config.gridSize);

    return {
        rows: [{
            type: 'row',
            columns: [column]
        }]
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

// generate rows with some columns to be used as a template
const generateRowTemplates = function () {
    const rowColumns = [1, 2, 3, 4]; // eslint-disable-line no-magic-numbers

    return rowColumns.map(numberOfColumns => {
        const columns = [];
        for (let i = 0; i < numberOfColumns; i++) {
            columns.push(utils.setColumnWidths({ content: [] }, config.gridSize / numberOfColumns));
        }
        return {
            name: `Row ${numberOfColumns}-column`,
            data: {
                type: 'row',
                columns
            }
        };
    });
};


export default new Vuex.Store({
    strict: Boolean(window.webpackHotUpdate), // warn on state mutations outside mutation handlers when in dev mode
    state: {
        editMode: true,
        selectionMode: false,
        selectedItem: null,
        resizeColumnInfo: null,
        layout: getEmptyLayout(),
        nodes: [],
        elements: generateRowTemplates()
    },
    getters: {
        getAllNodeIdsInLayout(state) {
            const allContentArrays = getAllContentArrays(state.layout.rows);
            return [].concat(...allContentArrays).filter(item => item.type === 'view').map(item => item.nodeID);
        }
    },
    mutations: {
        setLayout(state, layout) {
            // replace current layout with new one
            state.layout = JSON.parse(JSON.stringify(layout));
        },
        setNodes(state, nodes) {
            state.nodes = JSON.parse(JSON.stringify(nodes));
        },
        initialLayout(state) {
            const content = [];

            state.nodes.forEach(node => {
                content.push(utils.createViewFromNode(node));
            });

            const rows = [{
                type: 'row',
                columns: [utils.setColumnWidths({ content }, config.gridSize)]
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
                        utils.setColumnWidths(sibling, sibling.widthMD + delta);
                    }
                    break;
                }
            }

            // currently we don't support responsive layouts, so set all sizes
            utils.setColumnWidths(info.column, info.newWidth);
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
                            const lostWidthSplit = lostWidth / numberOfColumnsToSplit;
                            utils.setColumnWidths(sibling1, sibling1.widthMD + lostWidthSplit);
                            utils.setColumnWidths(sibling2, sibling2.widthMD + lostWidthSplit);
                        } else {
                            utils.setColumnWidths(sibling1, sibling1.widthMD + lostWidth);
                        }
                    } else if (sibling1) {
                        utils.setColumnWidths(sibling1, sibling1.widthMD + lostWidth);
                    } else {
                        utils.setColumnWidths(sibling2, sibling2.widthMD + lostWidth);
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

                state.selectedItem.columns.forEach(column => {
                    utils.setColumnWidths(column, width);
                });
            }
        },

        addColumn(state, row) {
            let newNumberOfColumns = row.columns.length + 1;
            const width = Math.floor(config.gridSize / newNumberOfColumns);
            row.columns.push({
                content: []
            });

            row.columns.forEach(column => {
                utils.setColumnWidths(column, width);
            });

            const totalWidth = width * newNumberOfColumns;
            if (totalWidth < config.gridSize) {
                const delta = config.gridSize - totalWidth;
                const column = row.columns[row.columns.length - 1];
                utils.setColumnWidths(column, column.widthMD + delta);
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
