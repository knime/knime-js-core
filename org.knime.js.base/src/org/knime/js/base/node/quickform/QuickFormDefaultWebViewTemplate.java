package org.knime.js.base.node.quickform;

import org.knime.core.node.web.WebDependency;
import org.knime.core.node.web.WebResourceLocator;
import org.knime.core.node.web.WebTemplate;

/**
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * 
 */
public class QuickFormDefaultWebViewTemplate implements WebTemplate {

    /**
     * {@inheritDoc}
     */
    @Override
    public WebResourceLocator[] getWebResources() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebDependency[] getDependencies() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNamespace() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInitMethodName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPullViewContentMethodName() {
        // TODO Auto-generated method stub
        return null;
    }

}
