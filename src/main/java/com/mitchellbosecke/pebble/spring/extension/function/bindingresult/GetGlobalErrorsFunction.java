/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.spring.extension.function.bindingresult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.mitchellbosecke.pebble.template.EvaluationContext;

/**
 * <p>
 * Function available to templates in Spring MVC applications in order to access the BindingResult of a form
 * </p>
 *
 * @author Eric Bussieres
 */
public class GetGlobalErrorsFunction extends BaseBindingResultFunction {
    private final MessageSource messageSource;

    public GetGlobalErrorsFunction(MessageSource messageSource) {
        super(PARAM_FORM_NAME);
        if (messageSource == null) {
            throw new IllegalArgumentException("In order to use the GetErrorsFunction, a bean of type "
                    + MessageSource.class.getName() + " must be configured");
        }
        this.messageSource = messageSource;
    }

    @Override
    public Object execute(Map<String, Object> args) {
        List<String> results = new ArrayList<>();
        String formName = (String) args.get(PARAM_FORM_NAME);

        EvaluationContext context = (EvaluationContext) args.get("_context");
        Locale locale = context.getLocale();
        BindingResult bindingResult = this.getBindingResult(formName, context);

        if (bindingResult != null) {
            for (ObjectError error : bindingResult.getGlobalErrors()) {
                String msg = this.messageSource.getMessage(error.getCode(), error.getArguments(),
                        error.getDefaultMessage(), locale);
                if (msg != null) {
                    results.add(msg);
                }
            }
        }
        return results;
    }
}
