package com.reliaquest.api.mapper;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.reliaquest.api.model.Employee;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;

public class EmployeeDeserializer extends StdDeserializer<Employee> {

    private final String PREFIX = "employee_";

    public EmployeeDeserializer() {
        super(Employee.class);
    }

    @Override
    public Employee deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        List<String> names = new ArrayList<>();
        ObjectNode modifiedNode = mapper.createObjectNode();
        node.fieldNames().forEachRemaining(names::add);
        names.stream()
                .filter(name -> StringUtils.startsWithIgnoreCase(name, PREFIX))
                .forEach(name -> {
                    String stripped = name.substring(PREFIX.length());
                    ((ObjectNode) node).set(stripped, node.get(name));
                    ((ObjectNode) node).remove(name);

                    modifiedNode.set(stripped, node.get(name));
                });

        // If using this deserializer, this will need to be changed to prevent infinite recursion
        return mapper.treeToValue(modifiedNode, Employee.class);
    }
    //    setSerializationInclusion(JsonInclude.Include.NON_NULL);
    //    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    //    configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
}
