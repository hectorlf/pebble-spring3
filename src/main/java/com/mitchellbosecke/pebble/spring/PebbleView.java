/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.spring;

import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.spring.context.Beans;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class PebbleView extends AbstractTemplateView {

    public static final String REQUEST_VARIABLE_NAME = "request";
    public static final String SESSION_VARIABLE_NAME = "session";

    private static final String BEANS_VARIABLE_NAME = "beans";
    private static final int NANOS_IN_SECOND = 1000000;
    private String characterEncoding = "UTF-8";
    // We declare a HashMap instead of Map in values to have the clone method
    private static final ConcurrentMap<ApplicationContext, HashMap<String, Object>> springBeansMap =
            new ConcurrentHashMap<ApplicationContext, HashMap<String, Object>>();
    /**
     * <p>
     * TIMER logger. This logger will output the time required for executing each template processing operation.
     * </p>
     * <p>
     * The value of this constant is <tt>com.mitchellbosecke.pebble.spring.PebbleView.TIMER</tt>. This allows you to set
     * a specific configuration and/or appenders for timing info at your logging system configuration.
     * </p>
     */
    private static final Logger TIMER_LOGGER = LoggerFactory.getLogger(PebbleView.class.getName() + ".timer");
    private PebbleEngine engine;
    private String templateName;

    @SuppressWarnings("unchecked")
    private static Map<String, Object> addSpringBeansVariable(Map<String, ?> model, ApplicationContext appctx) {
        HashMap<String, Object> variableMap = springBeansMap.get(appctx);
        if (variableMap == null) {
            variableMap = new HashMap<>();
            // We will use a singleton-per-appctx Beans instance
            variableMap.put(BEANS_VARIABLE_NAME, new Beans(appctx));
            springBeansMap.put(appctx, variableMap);
        }

        Map<String, Object> newVariables;
        synchronized (variableMap) {
            newVariables = (Map<String, Object>) variableMap.clone();
        }

        if (model != null) {
            newVariables.putAll(model);
        }

        return newVariables;
    }

    public void setPebbleEngine(PebbleEngine engine) {
        this.engine = engine;
    }

    public void setTemplateName(String name) {
        this.templateName = name;
    }
    
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        response.setContentType(this.getContentType());
        response.setCharacterEncoding(this.characterEncoding);

        long startNanos = System.nanoTime();

        PebbleTemplate template = this.engine.getTemplate(this.templateName);

        // Add beans context
        model = addSpringBeansVariable(model, this.getApplicationContext());

        // Add request
        model.put(REQUEST_VARIABLE_NAME, request);

        // Add session
        model.put(SESSION_VARIABLE_NAME, request.getSession(false));

        // Locale
        Locale locale = RequestContextUtils.getLocale(request);
        
        final Writer writer = response.getWriter();
        try {
            template.evaluate(writer, model, locale);
        }
        finally {
            writer.flush();
        }

        if (TIMER_LOGGER.isDebugEnabled()) {
            long endNanos = System.nanoTime();

            BigDecimal elapsed = BigDecimal.valueOf(endNanos - startNanos);
            BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf(NANOS_IN_SECOND), RoundingMode.HALF_UP);
            TIMER_LOGGER.debug(
                    "Pebble template \"{}\" with locale {} processed in {} nanoseconds (approx. {}ms)",
                    new Object[] { this.templateName, locale, elapsed, elapsedMs });
        }
    }
}
