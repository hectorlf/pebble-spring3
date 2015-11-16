/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.spring.extension.function.bindingresult;

import java.util.Map;

import org.springframework.validation.BindingResult;

import com.mitchellbosecke.pebble.template.EvaluationContext;

/**
 * <p>
 * Function available to templates in Spring MVC applications in order to access the BindingResult of a form
 * </p>
 *
 * @author Eric Bussieres
 */
public class HasErrorsFunction extends BaseBindingResultFunction {
    public HasErrorsFunction() {
        super(PARAM_FORM_NAME);
    }

    @Override
    public Object execute(Map<String, Object> args) {
        String formName = (String) args.get(PARAM_FORM_NAME);

        EvaluationContext context = (EvaluationContext) args.get("_context");
        BindingResult bindingResult = this.getBindingResult(formName, context);
        return bindingResult.hasErrors();
    }
}
