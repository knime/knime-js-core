import { shallowMount, createLocalVue } from '@vue/test-utils';
import AppConfigurationLayoutEditor from '~/src/components/AppConfigurationLayoutEditor';
import Vuex from 'vuex';
import * as workflowStoreConfig from '~/src/store/configurationLayoutEditor';
import Draggable from 'vuedraggable';
import KnimeView from '~/src/components/layout/KnimeView';

describe('AppConfigurationLayoutEditor.vue', () => {

    let localVue, store, mocks;

    beforeAll(() => {
        localVue = createLocalVue();
        localVue.use(Vuex);

        store = new Vuex.Store(workflowStoreConfig);

        store.state.layout = {
            rows: [{
                type: 'row',
                columns: [{
                    content: [{
                        nodeID: '1'
                    }]
                }]
            }, {
                type: 'row',
                columns: [{
                    content: [{
                        nodeID: '7'
                    }]
                }]
            }, {
                type: 'row',
                columns: [{
                    content: [{
                        nodeID: '2'
                    }]
                }]
            }]
        };

        mocks = { $store: store };
    });

    it('renders', () => {
        const wrapper = shallowMount(AppConfigurationLayoutEditor, {
            mocks
        });

        expect(wrapper.find(Draggable).exists()).toBe(true);

        let views = wrapper.findAll(KnimeView);
        const expectedViewCount = 3;
        expect(views.length).toBe(expectedViewCount);
        views.wrappers.forEach((view, index) => {
            expect(view.props('view')).toBe(store.state.layout.rows[index].columns[0].content[0]);
        });
    });

    it('renders empty message', () => {
        const wrapper = shallowMount(AppConfigurationLayoutEditor, {
            mocks
        });

        store.state.layout = {};

        expect(wrapper.find(Draggable).exists()).toBe(false);
        expect(wrapper.text()).toBe('This component doesn’t contain any configuration nodes.');
    });

});
