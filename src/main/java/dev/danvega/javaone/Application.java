package dev.danvega.javaone;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        McpServer.sync(new StdioServerTransportProvider(new ObjectMapper()))
                .serverInfo("javaone-mcp-server", "0.0.1")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .tools(new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool("get_presentations", "Get a list of all presentations from JavaOne",
                                "{\n" +
                                "  \"type\" : \"object\",\n" +
                                "  \"id\" : \"urn:jsonschema:Operation\",\n" +
                                "  \"properties\" : {\n" +
                                "    \"operation\" : {\n" +
                                "      \"type\" : \"string\"\n" +
                                "    }\n" +
                                "  }\n" +
                                "}"),
                        (exchange, arguments) -> {
                            List<Presentation> presentations = new PresentationTools().getPresentations();
                            List<McpSchema.Content> contents = new ArrayList<>();
                            for (Presentation presentation : presentations) {
                                contents.add(new McpSchema.TextContent(presentation.toString()));
                            }
                            return new McpSchema.CallToolResult(contents, false);
                        }
                ))
                .build();

        log.info("Starting JavaOne MCP Server...");
    }

}
