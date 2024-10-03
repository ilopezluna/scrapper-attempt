package ai.ilopezluna.smartscraper;

import ai.ilopezluna.smartscraper.services.Extractor;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.shaded.com.google.common.base.Splitter;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class App {

    @SneakyThrows
    public static void main(String[] args) {
        var ollama = new OllamaContainer(
                DockerImageName.parse("ilopezluna/llama3.2:0.3.12-3b")
                        .asCompatibleSubstituteFor("ollama/ollama")
        ).withReuse(true);

        ollama.start();
        ChatLanguageModel model =
                OllamaChatModel
                        .builder()
                        .baseUrl(ollama.getEndpoint())
                        .modelName("llama3.2:3b")
                        .temperature(0d)
                        .topK(1)
                        .topP(0.00001d)
                        .seed(42)
                        .logRequests(true)
                        .build();

        Document document = Jsoup.connect("https://ollama.com/library").get();
        document.getAllElements().forEach(Element::clearAttributes);
        document.select("script,style,meta").remove();
        String html = document.html();

        Extractor extractor = AiServices.create(Extractor.class, model);
        String result = StreamSupport.stream(Splitter
                        .fixedLength(5000)
                        .split(html)
                        .spliterator(), false)
                .map(extractor::extract)
                .collect(Collectors.joining());

        log.info("Response: {}", result);
    }
}
