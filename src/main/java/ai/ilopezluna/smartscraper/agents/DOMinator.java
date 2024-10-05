package ai.ilopezluna.smartscraper.agents;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface DOMinator {

	@SystemMessage("""
			Your are an HTML expert, you will be provided with an HTML document and a description of the elements to search for. Based on the provided description, your task is to return only the XPath expression that selects all the matching elements. The elements to find may be represented by various HTML tags (e.g., <li>, <div>, etc.), so inspect the structure carefully to match the user's description. Return only the XPath and no other output or explanation.
			""")
	@UserMessage("{{description}}")
	String getXpath(@V("description") String description);

}
