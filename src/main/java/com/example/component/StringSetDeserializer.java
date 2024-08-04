package com.example.component;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Deserialization
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/15 19:22
 */
public class StringSetDeserializer extends StdDeserializer<Set<String>> {


    public StringSetDeserializer() {
        this(null);
    }

    public StringSetDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Set<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException{
        JsonNode jsonNode = p.getCodec().readTree(p);
        Set<String> set = new HashSet<>();
        for(JsonNode node: jsonNode) {
            set.add(node.asText());
        }
        return set;
    }
}
