package com.andyholes.amabot.eventListener;

import com.andyholes.amabot.client.MessageClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class MessageCreateListener implements EventListener<MessageCreateEvent>{

    @Autowired
    private MessageClient messageClient;
    private String body;

    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    public Mono<Void> execute(MessageCreateEvent event) {
        return processCommand(event.getMessage());
    }

    private Mono<Void> processCommand(Message eventMessage) {
        String body = eventMessage.getContent();
        return Mono.just(eventMessage)
                .filter(message -> (message.getContent().startsWith("!ask ")))
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    try {
                        return channel.createMessage(messageClient.getResponse(body));
                    } catch (URISyntaxException | InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .then();
    }

    private String apply(Message message) {
        return body = message.getContent();
    }
}