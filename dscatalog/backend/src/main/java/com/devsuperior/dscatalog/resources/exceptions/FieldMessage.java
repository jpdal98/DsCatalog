package com.devsuperior.dscatalog.resources.exceptions;

import java.io.Serializable;

public class FieldMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	private String fieldName;
	private String Message;
	
	public FieldMessage() {
	
	}
	
	public FieldMessage(String fieldName, String message) { 
		this.fieldName = fieldName;
		this.Message = message;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}
}
