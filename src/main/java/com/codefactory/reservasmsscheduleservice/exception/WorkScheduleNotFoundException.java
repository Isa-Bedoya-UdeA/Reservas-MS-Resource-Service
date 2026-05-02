package com.codefactory.reservasmsscheduleservice.exception;

import java.util.UUID;

public class WorkScheduleNotFoundException extends RuntimeException {
    
    public WorkScheduleNotFoundException(String message) {
        super(message);
    }
    
    public WorkScheduleNotFoundException(UUID id) {
        super("Horario laboral no encontrado con id: " + id);
    }
}
