package app.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public final class JsonApi {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonApi() {
    }

    public static Response ok(Object value) {
        return json(Response.ok(), value);
    }

    public static Response created(Object value) {
        return json(Response.status(Response.Status.CREATED), value);
    }

    public static Response badRequest(String message) {
        return json(Response.status(Response.Status.BAD_REQUEST), new ErrorPayload(message));
    }

    public static Response notFound(String message) {
        return json(Response.status(Response.Status.NOT_FOUND), new ErrorPayload(message));
    }

    public static <T> T read(String json, Class<T> type) throws JsonProcessingException {
        return MAPPER.readValue(json, type);
    }

    public static <T> T convert(Object value, Class<T> type) {
        return MAPPER.convertValue(value, type);
    }

    private static Response json(Response.ResponseBuilder builder, Object value) {
        try {
            return builder.type(MediaType.APPLICATION_JSON).entity(MAPPER.writeValueAsString(value)).build();
        } catch (JsonProcessingException e) {
            return Response.serverError().type(MediaType.APPLICATION_JSON)
                    .entity("{\"error\":\"Failed to serialize response\"}").build();
        }
    }

    static final class ErrorPayload {
        private final String error;

        ErrorPayload(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}