package ai.ilopezluna.smartscraper.agents;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface Evaluator {

	@UserMessage("""
			Document: {{document}}
			Claim: {{claim}}
			""")
	String evaluate(@V("document") String html, @V("claim") String reference);

}
