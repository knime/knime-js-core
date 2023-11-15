import Vue from 'vue';
import config from '../config';
import utils from '../utils';

const getEmptyLayout = function () {
    const column = {
        content: []
    };
    utils.setColumnWidths(column, config.gridSize);

    return {
        rows: [{
            type: 'row',
            columns: [column]
        }],
        // when the layout is cleared, disable legacy mode by default
        parentLayoutLegacyMode: false
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
            name: `${numberOfColumns}-column`,
            data: {
                type: 'row',
                columns
            }
        };
    });
};

const generatePrintTemplates = function () {
    return [{
        name: 'Page Break',
        data: {
            type: 'row',
            pageBreakAfter: true
        }
    }];
};

// clean up the layout:
// - remove columns without a 'content' property
// - remove rows without a 'columns' property
// - remove multiple nodes with the same nodeID
// - add missing widthXS
// - remove duplicate widths if equal
const cleanLayout = function (layout) {
    const nodeIDs = [];

    const recursiveClean = function (layout) {
        const newLayout = layout.filter(item => {
            if (item.type === 'row') {
                if (Array.isArray(item.columns)) {
                    item.columns = item.columns.filter(column => {
                        if (Array.isArray(column.content)) {
                            column.content = recursiveClean(column.content);

                            // add widthXS if not existing
                            if (!column.hasOwnProperty('widthXS')) {
                                column.widthXS = column.widthSM || column.widthMD || column.widthLG ||
                                    column.widthXL || config.gridSize;
                            }

                            // remove all other widths if equal
                            const widthProps = ['widthXL', 'widthLG', 'widthMD', 'widthSM', 'widthXS'];
                            widthProps.forEach((prop, index) => {
                                // skip last item
                                if (index === widthProps.length - 1) {
                                    return;
                                }

                                if (column.hasOwnProperty(prop)) {
                                    const nextAvailableWidth = widthProps.slice(index + 1)
                                        .find(prop => column.hasOwnProperty(prop));

                                    if (column[prop] === column[nextAvailableWidth]) {
                                        delete column[prop];
                                    }
                                }
                            });

                            return true;
                        } else {
                            // remove column without 'content' array
                            return false;
                        }
                    });
                    return true;
                } else if (item.pageBreakAfter) {
                    // empty rows allowed if used for enforcing page breaks
                    return true;
                } else {
                    // remove rows without 'columns' array
                    return false;
                }
            } else if (item.hasOwnProperty('nodeID')) {
                // if nodes are loaded without legacy mode, ensure they respect the current settings
                if (item.type === 'view' && typeof item.useLegacyMode === 'undefined') {
                    item.useLegacyMode = layout.parentLayoutLegacyMode;
                }
                if (nodeIDs.includes(item.nodeID)) {
                    // remove duplicate nodes
                    return false;
                } else {
                    nodeIDs.push(item.nodeID);
                    return true;
                }
            } else {
                return true;
            }
        });

        return newLayout;
    };

    return {
        rows: recursiveClean(layout.rows),
        parentLayoutLegacyMode: layout.parentLayoutLegacyMode
    };
};


export const strict = Boolean(window.webpackHotUpdate); // warn on state mutations outside mutation handlers when in dev mode

export const state = () => ({
    dragging: null,
    resizeColumnInfo: null,
    layout: getEmptyLayout(),
    initialLayout: null,
    nodes: [],
    elements: generateRowTemplates(),
    print: generatePrintTemplates()
});

export const mutations = {
    setLayout(state, layout) {
        const cleanedLayout = cleanLayout(layout);

        const layoutAsString = JSON.stringify(cleanedLayout);

        // replace current layout with new one
        state.layout = JSON.parse(layoutAsString);

        // save as initial layout
        state.initialLayout = layoutAsString;
    },

    setLayoutByTextarea(state, layout) {
        const cleanedLayout = cleanLayout(layout);

        // replace current layout with new one
        state.layout = JSON.parse(JSON.stringify(cleanedLayout));
    },

    setNodes(state, nodes) {
        state.nodes = nodes;
    },

    resetLayout(state) {
        if (state.initialLayout) {
            // reset to initial layout
            state.layout = JSON.parse(state.initialLayout);
        }
    },

    clearLayout(state) {
        // reset to empty layout
        state.layout = getEmptyLayout();
    },

    setDragging(state, dragging) {
        state.dragging = dragging;
    },

    // called on column resize start and end
    setResizeColumnInfo(state, resizeColumnInfo) {
        if (resizeColumnInfo === null) {
            state.resizeColumnInfo = null;
            return;
        }

        // to prevent wrapping we need to resize the next sibling as well...
        const resizingColumn = resizeColumnInfo.column;
        const columns = getAllColumnArrays(state.layout.rows)
            .find(columnArray => columnArray.includes(resizingColumn));
        const nextSibling = columns[columns.indexOf(resizingColumn) + 1];
        const allOtherSiblings = columns.filter(column => ![resizingColumn, nextSibling].includes(column));
        const widthOfOtherSiblings = allOtherSiblings.reduce((total, column) => total + column.widthXS, 0);

        // ...therefore we save further information about the siblings to be used in resizeColumn()
        state.resizeColumnInfo = { ...resizeColumnInfo, columns, nextSibling, widthOfOtherSiblings };
    },

    resizeColumn(state, newWidth) {
        const resizeInfo = state.resizeColumnInfo;

        // min size for column
        if (newWidth < 1) {
            newWidth = 1;
        }

        // calc size of next sibling to prevent wrapping
        const currentWidth = resizeInfo.column.widthXS;
        let delta = currentWidth - newWidth;
        let newSiblingWidth = resizeInfo.nextSibling.widthXS + delta;
        if (newSiblingWidth < 1) {
            newSiblingWidth = 1;
        }

        // also make sure the total width doesn't exceed the gridSize
        const totalWidth = resizeInfo.widthOfOtherSiblings + newWidth + newSiblingWidth;
        if (totalWidth <= config.gridSize) {
            // currently we don't support responsive layouts, so set all sizes
            utils.setColumnWidths(resizeInfo.nextSibling, newSiblingWidth);
            utils.setColumnWidths(resizeInfo.column, newWidth);
        }
    },

    deleteColumn(state, columnToDelete) {
        const allColumnArrays = getAllColumnArrays(state.layout.rows);

        for (let columnArray of allColumnArrays) {
            let index = columnArray.indexOf(columnToDelete);
            if (index >= 0) {
                // resize siblings to fill total grid width
                const lostWidth = columnToDelete.widthXS;
                const sibling1 = columnArray[index - 1];
                const sibling2 = columnArray[index + 1];
                if (sibling1 && sibling2) {
                    // there is a left and right sibling, so split width to fill, if divisible by 2
                    const numberOfColumnsToSplit = 2;
                    if (lostWidth % numberOfColumnsToSplit === 0) {
                        const lostWidthSplit = lostWidth / numberOfColumnsToSplit;
                        utils.setColumnWidths(sibling1, sibling1.widthXS + lostWidthSplit);
                        utils.setColumnWidths(sibling2, sibling2.widthXS + lostWidthSplit);
                    } else {
                        utils.setColumnWidths(sibling1, sibling1.widthXS + lostWidth);
                    }
                } else if (sibling1) {
                    // only left sibling, so increase width
                    utils.setColumnWidths(sibling1, sibling1.widthXS + lostWidth);
                } else {
                    // only right sibling, so increase width
                    utils.setColumnWidths(sibling2, sibling2.widthXS + lostWidth);
                }

                // finally remove the column
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

    updateContentItemConfig(state, data) {
        const item = data.item;

        // if set to auto, get default resizeMethod from node
        if (data.config.resizeMethod === 'auto') {
            const defaultNode = state.nodes.find(node => node.nodeID === data.item.nodeID);
            data.config.resizeMethod = defaultNode && defaultNode.layout.resizeMethod.indexOf('view') === 0
                ? defaultNode.layout.resizeMethod
                : 'viewLowestElement';
        }

        // apply new config; delete unset props
        for (let prop in data.config) {
            const value = data.config[prop];
            if (value) {
                Vue.set(item, prop, value);
            } else {
                Vue.delete(item, prop);
            }
        }
    },

    // used by vuedraggable on drag&drop or reorder
    updateColumnContent(state, data) {
        data.column.content = data.newContent;
    },

    // used by vuedraggable on drag&drop or reorder
    updateFirstLevelRows(state, rows) {
        state.layout.rows = rows;
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
            utils.setColumnWidths(column, column.widthXS + delta);
        }
    },

    addNode(state, node) {
        const view = utils.createViewFromNode(node);

        // find last row
        const lastRow = state.layout.rows[state.layout.rows.length - 1];

        // find and add to first empty column (currently without supporting nesting)
        const emptyColumn = lastRow.columns.find(column => column.content.length === 0);
        if (emptyColumn) {
            emptyColumn.content.push(view);
        } else {
            // or last column, if no empty column exists
            lastRow.columns[lastRow.columns.length - 1].content.push(view);
        }
    },

    addElement(state, element) {
        state.layout.rows.push(element);
    },

    setUseLegacyMode(state, { useLegacyMode }) {
        // set parent layout legacy mode
        state.layout.parentLayoutLegacyMode = useLegacyMode;
        // set view legacy mode
        getAllContentArrays(state.layout.rows).forEach(contentArray => {
            contentArray.forEach(content => {
                if (content.type !== 'nestedLayout' && typeof content.useLegacyMode !== 'undefined') {
                    content.useLegacyMode = useLegacyMode;
                }
            });
        });
    }
};

export const getters = {
    nodeIdsInLayout(state) {
        const allContentArrays = getAllContentArrays(state.layout.rows);
        return [].concat(...allContentArrays)
            .filter(item => item.hasOwnProperty('nodeID'))
            .map(item => item.nodeID);
    },
    availableNodes(state, getters) {
        const nodeIdsInLayout = getters.nodeIdsInLayout;
        return state.nodes.filter(node => !nodeIdsInLayout.includes(node.nodeID));
    },
    isResponsiveLayout(state) {
        const allColumnArrays = getAllColumnArrays(state.layout.rows);
        const columWithResponsiveWidths = [].concat(...allColumnArrays).find(column => {
            if (column.hasOwnProperty('widthSM') || column.hasOwnProperty('widthMD') ||
                column.hasOwnProperty('widthLG') || column.hasOwnProperty('widthXL')) {
                return true;
            } else {
                return false;
            }
        });
        return Boolean(columWithResponsiveWidths);
    },
    isWrappingLayout(state) {
        const allColumnArrays = getAllColumnArrays(state.layout.rows);
        const firstWrappingRow = allColumnArrays.find(columns => {
            const totalWidth = columns.reduce((total, column) => total + column.widthXS, 0);
            return totalWidth > config.gridSize;
        });
        return Boolean(firstWrappingRow);
    },
    isLegacyModeOutOfSync(state) {
        return getAllContentArrays(state.layout.rows).some(contentArray => contentArray.some(content => {
            if (content.type !== 'nestedLayout' && typeof content.useLegacyMode !== 'undefined') {
                return content.useLegacyMode !== state.layout.parentLayoutLegacyMode;
            }
            return false;
        }));
    }
};
