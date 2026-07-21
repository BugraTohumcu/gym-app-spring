package org.bugra.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.bugra.dto.response.ErrorResponse;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ApiErrorResponses.class)
@ApiResponse(content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface ApiErrorResponse {

    @AliasFor(annotation = ApiResponse.class, attribute = "responseCode")
    String responseCode() default "404";

    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String value() default "";

    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "";
}
