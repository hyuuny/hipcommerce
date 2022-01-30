package com.hipcommerce.common.exceptions;

import com.hipcommerce.common.web.model.HttpStatusMessageException;
import org.springframework.http.HttpStatus;

public class UserDuplicatedException extends HttpStatusMessageException {

  public UserDuplicatedException(String message) {
    super(HttpStatus.BAD_REQUEST, "user.duplicated", message);
  }
}

