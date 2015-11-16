/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.spring.extension.function.bindingresult;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;

/**
 * Base class of the function interacting with the BindingResult
 *
 * @author Eric Bussieres
 */
public abstract class BaseBindingResultFunction implements Function {
    protected static final String PARAM_FIELD_NAME = "fieldName";
    protected static final String PARAM_FORM_NAME = "formName";
    private final List<String> argumentNames = new ArrayList<>();

    protected BaseBindingResultFunction(String... argumentsName) {
        for (String arg : argumentsName) {
            this.argumentNames.add(arg);
        }
    }

    @Override
    public List<String> getArgumentNames() {
        return this.argumentNames;
    }

    protected BindingResult getBindingResult(String formName, EvaluationContext context) {
        String attribute = BindingResult.class.getName() + "." + formName;
        return (BindingResult) context.get(attribute);
    }
}
