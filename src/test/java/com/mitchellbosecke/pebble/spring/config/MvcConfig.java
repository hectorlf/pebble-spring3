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
import org.springframework.web.servlet.ViewResolver;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.spring.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring.bean.SomeBean;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;

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
        return new PebbleEngine.Builder()
                .loader(this.templateLoader())
                .strictVariables(false)
                .extension(springExtension())
                .build();
    }
    
    @Bean
    public SpringExtension springExtension() {
        return new SpringExtension();
    }

    @Bean
    public Loader<?> templateLoader() {
        return new ClasspathLoader();
    }

    @Bean
    public ViewResolver viewResolver() {
        PebbleViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix("com/mitchellbosecke/pebble/spring/template/");
        viewResolver.setSuffix(".html");
        viewResolver.setPebbleEngine(this.pebbleEngine());
        return viewResolver;
    }
}
