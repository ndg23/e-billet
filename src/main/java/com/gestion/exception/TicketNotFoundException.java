package com.gestion.exception;

//TicketNotFoundException.java
public class TicketNotFoundException extends RuntimeException {
public TicketNotFoundException(String message) {
   super(message);
}
}