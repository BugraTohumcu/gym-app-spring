package org.bugra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiErrorResponses({
        @ApiErrorResponse(responseCode = "400", description = "Invalid JSON payload or formatting issue"),
        @ApiErrorResponse(responseCode = "422", description = "Validation failed for request fields")
})
public @interface ApiValidationErrors {
}