package com.walletra.event_consumer_service.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "consumed_event")
@Entity
public class ConsumedEvent {
	
	private UUID consumedEventId;
	
	private String eventType;
	
	private String sourceService;
	
	private UUID referenceId;
	
	private String payload;
	
	private LocalDateTime receivedAt;

}
