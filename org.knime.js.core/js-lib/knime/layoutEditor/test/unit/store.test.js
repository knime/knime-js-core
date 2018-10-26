import { assert } from 'chai';

import store from '~/src/store';

const validLayout = {
    rows: [
        {
            type: 'row',
            columns: [
                {
                    content: [{
                        type: 'view',
                        scrolling: false,
                        nodeID: '1',
                        resizeMethod: 'aspectRatio4by3',
                        autoResize: true,
                        sizeHeight: true,
                        sizeWidth: false,
                        isFirst: true
                    }, {
                        type: 'row',
                        columns: [
                            {
                                content: [],
                                widthXS: 12,
                                widthSM: 12,
                                widthMD: 12,
                                widthLG: 12,
                                widthXL: 12
                            }
                        ]
                    }],
                    widthXS: 12,
                    widthSM: 12,
                    widthMD: 12,
                    widthLG: 12,
                    widthXL: 12
                }
            ]
        }
    ]
};

describe('store', () => {
    it('loads valid layout', () => {
        store.commit('setLayout', validLayout);
        assert.deepEqual(store.state.layout, validLayout);
    });

    it('cleans invalid layout', () => {
        const invalidLayout = {
            rows: [
                { type: 'row' }, // invalid
                {
                    type: 'row',
                    columns: [
                        {}, // invalid
                        {
                            content: [{
                                type: 'view',
                                scrolling: false,
                                nodeID: '1',
                                resizeMethod: 'aspectRatio4by3',
                                autoResize: true,
                                sizeHeight: true,
                                sizeWidth: false,
                                isFirst: true // just to check if first duplicate item is removed
                            },
                            {
                                type: 'view',
                                scrolling: false,
                                nodeID: '1', // invalid because of duplicate
                                resizeMethod: 'aspectRatio4by3',
                                autoResize: true,
                                sizeHeight: true,
                                sizeWidth: false
                            },
                            {
                                type: 'row' // invalid
                            },
                            {
                                type: 'row',
                                columns: [
                                    {}, // invalid
                                    {
                                        content: [{
                                            type: 'view',
                                            scrolling: false,
                                            nodeID: '1', // invalid because of duplicate
                                            resizeMethod: 'aspectRatio4by3',
                                            autoResize: true,
                                            sizeHeight: true,
                                            sizeWidth: false
                                        }],
                                        widthXS: 12,
                                        widthSM: 12,
                                        widthMD: 12,
                                        widthLG: 12,
                                        widthXL: 12
                                    }
                                ]
                            }],
                            widthXS: 12,
                            widthSM: 12,
                            widthMD: 12,
                            widthLG: 12,
                            widthXL: 12
                        },
                        {}, // invalid
                        {} // invalid
                    ]
                },
                { type: 'row' } // invalid
            ]
        };

        store.commit('setLayout', invalidLayout);
        assert.deepEqual(store.state.layout, validLayout);
    });

    it('gets all nodeIds used in the layout', () => {
        store.commit('setLayout', validLayout);
        assert.deepEqual(store.getters.getAllNodeIdsInLayout, ['1']);
    });
});
