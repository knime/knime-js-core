import { createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';

import * as storeConfig from '../../src/store';

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
                                widthXS: 6
                            },
                            {
                                content: [],
                                widthXS: 6
                            }
                        ]
                    }],
                    widthXS: 12
                }
            ]
        }
    ]
};

describe('store', () => {
    let store, localVue;

    beforeAll(() => {
        localVue = createLocalVue();
        localVue.use(Vuex);
    });

    beforeEach(() => {
        store = new Vuex.Store(storeConfig);
    });

    it('loads valid columns and rows', () => {
        store.commit('setLayout', validLayout);
        expect(store.state.layout).toEqual(validLayout);
    });


    it('cleans invalid columns and rows', () => {
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
                                        widthXS: 6
                                    },
                                    {
                                        content: [],
                                        widthXS: 6
                                    }
                                ]
                            }],
                            widthXS: 12
                        },
                        {}, // invalid
                        {} // invalid
                    ]
                },
                { type: 'row' } // invalid
            ]
        };

        store.commit('setLayout', invalidLayout);
        expect(store.state.layout).toEqual(validLayout);
    });


    it('cleans invalid column widths', () => {
        const invalidColumns = {
            rows: [
                {
                    type: 'row',
                    columns: [
                        {
                            content: [{
                                type: 'row',
                                columns: [
                                    {
                                        content: [],
                                        widthMD: 6 // invalid because no widthXS exists
                                    },
                                    {
                                        content: [],
                                        widthXL: 10 // invalid because no widthXS exists
                                    }
                                ]
                            }],
                            // widthXS missing
                            widthMD: 12, // invalid because equal
                            widthLG: 12, // invalid because equal
                            widthXL: 12 // invalid because equal
                        },
                        {
                            content: [],
                            // widthXS missing
                            widthMD: 6,
                            widthLG: 7,
                            widthXL: 8
                        }
                    ]
                }
            ]
        };

        const validColumns = {
            rows: [
                {
                    type: 'row',
                    columns: [
                        {
                            content: [{
                                type: 'row',
                                columns: [
                                    {
                                        content: [],
                                        widthXS: 6
                                    },
                                    {
                                        content: [],
                                        widthXS: 10
                                    }
                                ]
                            }],
                            widthXS: 12
                        },
                        {
                            content: [],
                            widthXS: 6,
                            widthLG: 7,
                            widthXL: 8
                        }
                    ]
                }
            ]
        };

        store.commit('setLayout', invalidColumns);
        expect(store.state.layout).toEqual(validColumns);
    });


    it('gets all nodeIds used in the layout', () => {
        store.commit('setLayout', validLayout);
        expect(store.getters.nodeIdsInLayout).toEqual(['1']);
    });

    it('detects responsive layout', () => {
        const staticLayout = {
            rows: [
                {
                    type: 'row',
                    columns: [
                        {
                            content: [{
                                type: 'row',
                                columns: [
                                    {
                                        content: [],
                                        widthXS: 6
                                    },
                                    {
                                        content: [],
                                        widthXS: 6
                                    }
                                ]
                            }],
                            widthXS: 12
                        }
                    ]
                }
            ]
        };
        const responsiveLayout = {
            rows: [
                {
                    type: 'row',
                    columns: [
                        {
                            content: [{
                                type: 'row',
                                columns: [
                                    {
                                        content: [],
                                        widthXS: 6
                                    },
                                    {
                                        content: [],
                                        widthXS: 6,
                                        widthSM: 7,
                                        widthMD: 8,
                                        widthLG: 9,
                                        widthXL: 10
                                    }
                                ]
                            }],
                            widthXS: 12
                        }
                    ]
                }
            ]
        };

        store.commit('setLayout', staticLayout);
        expect(store.getters.isResponsiveLayout).toBeFalsy();

        store.commit('setLayout', responsiveLayout);
        expect(store.getters.isResponsiveLayout).toBeTruthy();
    });

    it('detects wrapping layout', () => {
        const normalLayout = {
            rows: [
                {
                    type: 'row',
                    columns: [
                        {
                            content: [],
                            widthXS: 6
                        },
                        {
                            content: [],
                            widthXS: 6
                        }
                    ]
                }
            ]
        };
        const wrappingLayout = {
            rows: [
                {
                    type: 'row',
                    columns: [
                        {
                            content: [],
                            widthXS: 6
                        },
                        {
                            content: [],
                            widthXS: 7
                        }
                    ]
                }
            ]
        };

        store.commit('setLayout', normalLayout);
        expect(store.getters.isWrappingLayout).toBeFalsy();

        store.commit('setLayout', wrappingLayout);
        expect(store.getters.isWrappingLayout).toBeTruthy();
    });

});
