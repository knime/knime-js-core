import { createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';

import * as storeConfig from '../../src/store/nodeSorter';

describe('store', () => {
    let store, localVue;

    beforeAll(() => {
        localVue = createLocalVue();
        localVue.use(Vuex);
    });

    beforeEach(() => {
        store = new Vuex.Store(storeConfig);
    });

    it('has valid default layout', () => {
        expect(store.state.layout).toEqual({
            rows: [{
                type: 'row',
                columns: [{
                    content: []
                }]
            }]
        });
    });

    it('creates a copy of the layout on setLayout', () => {
        let newLayout = { rows: [] };
        store.commit('setLayout', newLayout);
        expect(store.state.layout).not.toBe(newLayout);
        expect(store.state.layout).toStrictEqual(newLayout);
    });

    it('allows setting nodes', () => {
        let newNodes = [{}];
        store.commit('setNodes', newNodes);
        expect(store.state.nodes).toStrictEqual(newNodes);
    });

    it('allows updating and gettings rows', () => {
        let newRows = [{}];
        store.commit('updateRows', newRows);
        expect(store.getters.getRows).toStrictEqual(newRows);
    });

});
