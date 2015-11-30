/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.Loader;

public class PebbleViewResolver extends AbstractTemplateViewResolver implements ViewResolver, InitializingBean {

    private PebbleEngine pebbleEngine;

    private Loader<?> templateLoader;

    public PebbleViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.templateLoader = this.pebbleEngine.getLoader();
        this.templateLoader.setPrefix(this.getPrefix());
        this.templateLoader.setSuffix(this.getSuffix());
    }

    @Required
    public void setPebbleEngine(PebbleEngine pebbleEngine) {
        this.pebbleEngine = pebbleEngine;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        PebbleView view = (PebbleView) super.buildView(viewName);
        view.setTemplateName(viewName);
        view.setPebbleEngine(this.pebbleEngine);

        return view;
    }

    @Override
    protected Class<?> requiredViewClass() {
        return PebbleView.class;
    }
}
