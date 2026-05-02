package com.codefactory.reservasmsscheduleservice.exception;

import java.util.UUID;

public class ScheduleBlockNotFoundException extends RuntimeException {
    
    public ScheduleBlockNotFoundException(String message) {
        super(message);
    }
    
    public ScheduleBlockNotFoundException(UUID id) {
        super("Bloqueo de horario no encontrado con id: " + id);
    }
}
