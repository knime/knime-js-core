import { shallowMount, createLocalVue } from '@vue/test-utils';
import Vuex from 'vuex';

import KnimeView from '~/src/components/layout/KnimeView';

describe('KnimeView.vue', () => {

    let localVue, store, mocks;

    beforeAll(() => {
        localVue = createLocalVue();
        localVue.use(Vuex);

        store = new Vuex.Store({
            state: {
                nodes: [{
                    nodeID: 1,
                    name: 'Node name 1',
                    type: 'widget',
                    icon: 'iconData'
                }, {
                    nodeID: 2,
                    name: 'Node name 2',
                    type: 'widget',
                    icon: 'iconData',
                    description: 'Node description 2',
                    availableInView: false
                }, {
                    nodeID: 3,
                    name: 'Node name 3',
                    type: 'widget',
                    icon: 'iconData',
                    availableInDialog: false
                }]
            }
        });

        mocks = { $store: store };
    });

    it('renders', () => {
        const wrapper = shallowMount(KnimeView, {
            mocks,
            propsData: {
                view: {
                    nodeID: 1
                }
            }
        });
        let nodeName = 'Node name 1';

        expect(wrapper.classes()).toContain('knimeView');
        expect(wrapper.classes()).toContain('widget');
        
        let nodeDiv = wrapper.find(`div[title="${nodeName}"]`);
        expect(nodeDiv.exists()).toBeTruthy();
        expect(nodeDiv.find('img').attributes('src')).toBe('iconData');
        expect(nodeDiv.text()).toContain(nodeName);
        expect(nodeDiv.text()).toContain('Node\u00a01');

        expect(nodeDiv.find('.description').exists()).toBeFalsy();
        expect(wrapper.find('main').text()).not.toContain('(disabled in node usage)');
    });

    it('renders description', () => {
        let wrapper = shallowMount(KnimeView, {
            mocks,
            propsData: {
                view: {
                    nodeID: 2
                }
            }
        });

        expect(wrapper.find('.description').text()).toBe('Node description 2');
    });

    it('renders disabled hint', () => {
        let wrapper = shallowMount(KnimeView, {
            mocks,
            propsData: {
                view: {
                    nodeID: 2
                }
            }
        });
        expect(wrapper.find('main').text()).toContain('(disabled in node usage)');

        wrapper = shallowMount(KnimeView, {
            mocks,
            propsData: {
                view: {
                    nodeID: 3
                }
            }
        });
        expect(wrapper.find('main').text()).toContain('(disabled in node usage)');
    });

    it('renders resize class', () => {
        let wrapper = shallowMount(KnimeView, {
            mocks,
            propsData: {
                view: {
                    nodeID: 1,
                    resizeMethod: 'lowestElement'
                }
            }
        });
        expect(wrapper.classes()).toContain('lowestElement');
    });

    it('renders size styles', () => {
        let wrapper = shallowMount(KnimeView, {
            mocks,
            propsData: {
                view: {
                    nodeID: 1,
                    minWidth: 10,
                    maxWidth: 20,
                    minHeight: '30px',
                    maxHeight: 40
                }
            }
        });
        expect(wrapper.attributes('style')).toBe(
            'min-width: 10px; max-width: 20px; min-height: 30px; max-height: 40px;'
        );

        wrapper = shallowMount(KnimeView, {
            mocks,
            propsData: {
                view: {
                    nodeID: 1,
                    minWidth: 0,
                    maxWidth: undefined
                }
            }
        });
        expect(wrapper.attributes('style')).toBeUndefined();
    });

    it('renders non-existing node', () => {
        const wrapper = shallowMount(KnimeView, {
            mocks,
            propsData: {
                view: {
                    nodeID: 999
                }
            }
        });

        expect(wrapper.classes()).toEqual(expect.arrayContaining(['knimeView', 'missing']));
        expect(wrapper.find('main').text()).toBe('Node\u00a0999 (missing in workflow)');
    });

});
