# MCP Server

## A Model Context Protocol Server 

JavaOne MCP Server is a lightweight Java application that implements the 
Model Context Protocol (MCP), allowing AI models to access information. 
This server exposes data through standardized MCP tools, making it easy 
to integrate with AI assistants that support the protocol.

## Project Requirements

- Java 18
- Maven 3.8+
- Model Context Protocol SDK 0.9.0
- SLF4J for logging

## Dependencies

This project relies on the following key components:

```xml
<!-- MCP SDK -->
<dependency>
    <groupId>io.modelcontextprotocol.sdk</groupId>
    <artifactId>mcp</artifactId>
</dependency>

<!-- Logging -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
</dependency>
```

## Getting Started

After cloning the repository, you can build the project using Maven:

```bash
mvn clean package
```

This will create an executable JAR file in the `target` directory with all dependencies included.

## How to Run the Application

Execute the JAR file to start the MCP server:

```bash
java -jar target/test-mcp-server-0.0.1.jar
```

The application starts an MCP server that communicates via standard 
input/output (STDIO) and provides access to data.

## Understanding the Project Structure

### Core Components

- **Application.java**: Main entry point that configures and starts the MCP server

### MCP Server Configuration

The server is configured with:

- STDIO transport provider for communication
- Server information (name and version)
- Tool capabilities for accessing presentation data

```java
McpSyncServer syncServer = McpServer.sync(transportProvider)
    .serverInfo("test-mcp-server", "0.0.1")
    .capabilities(McpSchema.ServerCapabilities.builder()
            .tools(true)
            .logging()
            .build())
    .tools(syncToolSpecification)
    .build();
```

### Available Tools

The server exposes the following MCP tool:

- **get_list**: Returns a list of all items by categories

## Testing with MCP Inspector

The MCP Inspector is a helpful tool for testing and debugging your MCP server. 
Follow these steps to test your MCP Server:

1. Install Node.js if you haven't already
2. Navigate to your project directory and get the absolute path to the JAR file:

```bash
# On Linux/macOS
FULL_PATH=$(pwd)/target/test-mcp-server-0.0.1.jar
echo $FULL_PATH

# On Windows PowerShell
$FULL_PATH="$(Get-Location)\target\test-mcp-server-0.0.1.jar"
echo $FULL_PATH
```

3. Run the MCP Inspector with your server using the full path:

```bash
npx @modelcontextprotocol/inspector java -jar $FULL_PATH
```

4. In the Inspector interface:
    - Verify the server connection in the connection pane
    - Navigate to the "Tools" tab to see the `get_list` tool
    - Test the tool by clicking on it and viewing the response
    - Monitor logs in the Notifications pane

## Integrating with Claude Desktop

To use this MCP server with Claude Desktop, add the following configuration:

1. First, get the absolute path to your JAR file:

```bash
# On Linux/macOS
FULL_PATH=$(pwd)/target/test-mcp-server-0.0.1.jar
echo $FULL_PATH

# On Windows PowerShell
$FULL_PATH="$(Get-Location)\target\test-mcp-server-0.0.1.jar"
echo $FULL_PATH
```

2. Open Claude Desktop preferences
3. Navigate to the "MCP Servers" section
4. Add a new server with the following configuration (replace the path with your actual full path):

```json
{
  "javaone-mcp": {
    "command": "java",
    "args": [
      "-jar",
      "$FULL_PATH"
    ]
  }
}
```

### Creating Additional MCP Tools

Implement more tools to expose different functionality:

```java
// Example of adding a tool to search presentations by title
var searchToolSpec = new McpServerFeatures.SyncToolSpecification(
    new McpSchema.Tool("search_items", "Search items by title", searchSchema),
    (exchange, arguments) -> {
        String query = arguments.get("query").asText();
        List<Presentation> results = presentationTools.searchPresentations(query);
        // Convert results to MCP content and return
    }
);
```

### Implementing Advanced Features

- Add resource subscriptions for real-time updates
- Implement asynchronous tools for long-running operations
- Add authentication and authorization mechanisms

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the [MIT License](LICENSE).

## Further Resources

- [Model Context Protocol Specification](https://modelcontextprotocol.github.io/spec/)
- [MCP Inspector Documentation](https://modelcontextprotocol.github.io/docs/tutorials/inspector)
- [MCP Debugging Guide](https://modelcontextprotocol.github.io/docs/tutorials/debugging)