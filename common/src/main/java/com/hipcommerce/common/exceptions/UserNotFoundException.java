package com.hipcommerce.common.exceptions;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException() {
    this("User not found.");
  }

  public UserNotFoundException(String msg) {
    super(msg);
  }

  public UserNotFoundException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
