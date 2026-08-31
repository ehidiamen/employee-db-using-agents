import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";

// Define our code review tool
const server = new Server({
  name: "code-review-mcp",
  version: "1.0.0",
}, {
  capabilities: {
    tools: {}
  }
});

// Register the tool
server.setRequestHandler(ListToolsRequestSchema, async () => {
  return {
    tools: [{
      name: "analyze_code_quality",
      description: "Analyze code for quality issues",
      inputSchema: {
        type: "object",
        properties: {
          code: { type: "string", description: "Code to analyze" },
          language: { 
            type: "string", 
            enum: ["java", "typescript", "python"],
            description: "Programming language" 
          }
        },
        required: ["code", "language"]
      }
    }]
  };
});

// Implement the tool
server.setRequestHandler(CallToolRequestSchema, async (request) => {
  if (request.params.name === "analyze_code_quality") {
    const { code, language } = request.params.arguments as { code: string; language: string };
    
    // In reality, this would call your existing code quality tools
    // For demonstration, return a mock analysis
    return {
      content: [{
        type: "text",
        text: JSON.stringify({
          quality: "good",
          issues: 0,
          suggestions: [
            "Consider using more descriptive variable names",
            "Add Javadoc comments for public methods"
          ]
        }, null, 2)
      }]
    };
  }
  
  throw new Error("Unknown tool");
});

// Start the server
const transport = new StdioServerTransport();
server.connect(transport).catch(console.error);

console.error("Code Review MCP Server running on stdio");