/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.spring.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.ViewResolver;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring.bean.SomeBean;

/**
 * Spring configuration for unit test
 *
 * @author Eric Bussieres
 */
@Configuration
public class MvcConfig {
    @Bean
    @Qualifier("foo")
    public SomeBean foo() {
        return new SomeBean();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("com.mitchellbosecke.pebble.spring.template.messages");

        return messageSource;
    }

    @Bean
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine(this.templateLoader());
    }

    @Bean
    public Loader templateLoader() {
        return new ServletLoader(new MockServletContext());
    }

    @Bean
    public ViewResolver viewResolver() {
        PebbleViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix("/com/mitchellbosecke/pebble/spring/template/");
        viewResolver.setSuffix(".html");
        viewResolver.setPebbleEngine(this.pebbleEngine());
        return viewResolver;
    }
}
