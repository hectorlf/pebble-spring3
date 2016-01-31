/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.spring.extension;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.spring.extension.function.MessageSourceFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.GetAllErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.GetFieldErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.GetGlobalErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.HasErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.HasFieldErrorsFunction;
import com.mitchellbosecke.pebble.spring.extension.function.bindingresult.HasGlobalErrorsFunction;

/**
 * <p>
 * Extension for PebbleEngine to add spring functionality
 * </p>
 *
 * @author Eric Bussieres
 */
public class SpringExtension extends AbstractExtension {
    @Autowired(required = false)
    private MessageSource messageSource;

    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<String, Function>();
        functions.put("message", new MessageSourceFunction(this.messageSource));
        functions.put("hasErrors", new HasErrorsFunction());
        functions.put("hasGlobalErrors", new HasGlobalErrorsFunction());
        functions.put("hasFieldErrors", new HasFieldErrorsFunction());
        functions.put("getAllErrors", new GetAllErrorsFunction(this.messageSource));
        functions.put("getGlobalErrors", new GetGlobalErrorsFunction(this.messageSource));
        functions.put("getFieldErrors", new GetFieldErrorsFunction(this.messageSource));
        return functions;
    }
}
