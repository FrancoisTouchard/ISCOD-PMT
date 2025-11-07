package com.iscod.pmt.utils;

import java.util.List;

import com.iscod.pmt.models.Task;

public class FieldFormatterUtils {
	
	private FieldFormatterUtils() {
        throw new IllegalStateException("Classe utilitaire");
    }
	
	public static String getFieldValue(Task task, String fieldName) {
	    return switch(fieldName) {
	        case "name" -> task.getName();
	        case "description" -> task.getDescription() != null ? task.getDescription() : "";
	        case "dueDate" -> task.getDueDate() != null ? task.getDueDate().toString() : "";
	        case "endDate" -> task.getEndDate() != null ? task.getEndDate().toString() : "";
	        case "priority" -> task.getPriority() != null ? task.getPriority().toString() : "";
	        case "status" -> task.getStatus() != null ? task.getStatus().toString() : "";
	        case "assigneeIds" -> task.getAssignments().isEmpty() ? "(aucun)" : 
	            task.getAssignments().stream()
	                .map(a -> a.getUser().getId().toString())
	                .reduce((a, b) -> a + ", " + b)
	                .orElse("(aucun)");
	        default -> "";
	    };
	}
	
	public static String convertValueToString(String fieldName, Object value) {
	    if (value == null) {
	        return "";
	    }
	    
	    return switch(fieldName) {
	        case "name", "description" -> (String) value;
	        case "dueDate", "endDate" -> value instanceof String ? (String) value : value.toString();
	        case "priority", "status" -> value instanceof String ? (String) value : value.toString();
	        case "assigneeIds" -> {
	            List<String> assigneeIds = ((List<?>) value)
	                .stream()
	                .map(Object::toString)
	                .toList();
	            yield assigneeIds.isEmpty() ? "(aucun)" : String.join(", ", assigneeIds);
	        }
	        default -> value.toString();
	    };
	}

}
