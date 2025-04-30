package com.codenjoy;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        McpServer.sync(new StdioServerTransportProvider(new ObjectMapper()))
                .serverInfo("test-mcp-server", "0.0.1")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .tools(new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool("get_list", "Get a list of items based on category",
                                """
                                {
                                  "type" : "object",
                                  "id" : "urn:jsonschema:Operation",
                                  "properties" : {
                                    "category" : {
                                      "type" : "string"
                                    }
                                  }
                                }"""),
                        (exchange, arguments) -> {
                            String category = (String) arguments.get("category");
                            if (category == null) {
                                return new McpSchema.CallToolResult(
                                        List.of(new McpSchema.TextContent("No category provided")),
                                        true); // this is error
                            }
                            switch (category) {
                                case "fruits" -> {
                                    return new McpSchema.CallToolResult(
                                            List.of(new McpSchema.TextContent("""
                                                            { "category": apple,
                                                              "items": [
                                                                { "name": "banana" },
                                                                { "name": "orange" },
                                                                { "name": "grape" }
                                                              ]
                                                            }""")),
                                            false);
                                }
                                case "vegetables" -> {
                                    return new McpSchema.CallToolResult(
                                            List.of(new McpSchema.TextContent("""
                                                            { "category": vegetables,
                                                              "items": [
                                                                { "name": "broccoli" },
                                                                { "name": "spinach" },
                                                                { "name": "kale" }
                                                              ]
                                                            }""")),
                                            false);
                                }
                                case "all" -> {
                                    return new McpSchema.CallToolResult(
                                            List.of(new McpSchema.TextContent("""
                                                            [
                                                              { "category": fruits,
                                                                "items": [
                                                                  { "name": "banana" },
                                                                  { "name": "orange" },
                                                                  { "name": "grape" }
                                                                ]
                                                              },
                                                              { "category": vegetables,
                                                                "items": [
                                                                  { "name": "broccoli" },
                                                                  { "name": "spinach" },
                                                                  { "name": "kale" }
                                                                ]
                                                              }
                                                            ]""")),
                                            false);
                                }
                                default -> {
                                    return new McpSchema.CallToolResult(
                                            List.of(new McpSchema.TextContent("Unknown category")),
                                            true); // this is error
                                }
                            }
                        }
                ),
                        new McpServerFeatures.SyncToolSpecification(
                                new McpSchema.Tool("search_items", "Search items by title",
                                        """
                                        {
                                          "type" : "object",
                                          "id" : "urn:jsonschema:Operation",
                                          "properties" : {
                                            "query" : {
                                              "type" : "string"
                                            }
                                          }
                                        }"""),
                                (exchange, arguments) -> {
                                    String query = (String) arguments.get("query");
                                    if (query == null) {
                                        return new McpSchema.CallToolResult(
                                                List.of(new McpSchema.TextContent("No query provided")),
                                                true); // this is error
                                    }
                                    Map<String, String> all = Map.of(
                                            "broccoli", "vegetable",
                                            "spinach", "vegetable",
                                            "kale", "vegetable",
                                            "banana", "banana",
                                            "orange", "fruit",
                                            "grape", "fruit"
                                    );

                                    String json = all.entrySet().stream()
                                            .filter(entry -> entry.getKey().contains(query))
                                            // group by category (value)
                                            .collect(groupingBy(Map.Entry::getValue,
                                                    mapping(Map.Entry::getKey, toList())))
                                            .entrySet().stream()
                                            // then build json
                                            .map(entry -> String.format("""
                                                            { "category": %s,
                                                              "items": [
                                                                %s
                                                              ]
                                                            }""",
                                                    entry.getKey(),
                                                    entry.getValue().stream()
                                                            .map(item -> String.format("{ \"name\": \"%s\" }", item))
                                                            .collect(joining(", "))))
                                            .collect(joining(", ", "[", "]"));

                                    return new McpSchema.CallToolResult(
                                            List.of(new McpSchema.TextContent(json)),
                                            false);
                                }
                        )
                )
                .build();

        // this is how we communicate over MCP in StdioServerTransportProvider
        System.out.println("Test message via stdout. This is main communication channel.");
        System.err.println("Error message via stderr. This is how we inform client not only about errors, but also about important events.");
        log.info("Info message via logger. This is how we inform about important events.");
        log.error("Error message via logger. Same like above, but with error level.");
    }

}
