package ai.ilopezluna.smartscraper.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Extractor {

    @SystemMessage("""
                    You are a helpful assistant proficient in extracting data from HTML documents.
            
                    Extract the following information from the provided HTML content:
                    1- Model Name: The exact name of the model (e.g., 'all-minilm').
                    2- Model Tags: Any associated tags or labels related to the model (e.g., '22m').
                    3- Model Description: A brief description or summary of the model (e.g., 'Embedding models on very large sentence-level datasets').
            
                    Output Format:
                    Return the extracted information in the following JSON-like structure:
            
                    {
                      model: {
                        name: "name of the model",
                        tags: ["tag1", "tag2", "tagn"],
                        description: "description of the model"
                      }
                    }
            
                    Only respond with the extracted information. No additional text is required.
            
            """)
    @UserMessage("HTML Content: {{it}}")
    String extract(String html);
}
