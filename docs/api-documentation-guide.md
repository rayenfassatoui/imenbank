# API Documentation Guide for ImenBank

This guide explains how to document REST APIs in the ImenBank system using SpringDoc OpenAPI.

## Getting Started

SpringDoc OpenAPI has been integrated into the project with the following configuration:

- API docs URL: `/api-docs`
- Swagger UI URL: `/swagger-ui.html`

## How to Document Controllers

Follow these steps to document each controller:

1. Add class-level annotations:
   ```java
   @Tag(name = "Controller Name", description = "Brief description of the controller's purpose")
   ```

2. Add method-level annotations:
   ```java
   @Operation(summary = "Short summary", description = "Detailed description of what the endpoint does")
   @ApiResponses(value = {
       @ApiResponse(responseCode = "200", description = "Success response"),
       @ApiResponse(responseCode = "400", description = "Bad request"),
       @ApiResponse(responseCode = "404", description = "Resource not found"),
       @ApiResponse(responseCode = "500", description = "Internal server error")
   })
   ```

3. Document parameters:
   ```java
   @Parameter(description = "Description of parameter", required = true)
   ```

## How to Document DTOs

Add Schema annotations to DTO classes and their fields:

```java
@Schema(description = "Description of this DTO")
public class ExampleDTO {
    
    @Schema(description = "Description of field", example = "Example value", required = true)
    private String field;
}
```

## Controllers to Document

Ensure all of the following controllers are documented:

1. ✅ FundsController (completed)
2. AuthController
3. RequestController
4. TeamController
5. UserController

## DTOs to Document

Ensure all of the following DTOs are documented:

1. ✅ TransactionDTO (completed)
2. TransactionReportDTO
3. RequestDTO
4. DriverDTO
5. TransporterDTO
6. EmployeeDTO
7. UserDTO

## Best Practices

1. **Be Specific**: Provide clear descriptions of what each endpoint does.
2. **Include Examples**: Use realistic examples in Schema annotations.
3. **Document All Responses**: Include all possible response codes and their meanings.
4. **Keep Documentation Up-to-Date**: Update the documentation when changing an endpoint.

## Testing Documentation

To verify your documentation:

1. Start the application
2. Navigate to: `http://localhost:8080/swagger-ui.html`
3. Check that all endpoints are properly documented
4. Test the "Try it out" functionality for each endpoint 